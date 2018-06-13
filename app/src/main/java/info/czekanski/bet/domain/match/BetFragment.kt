package info.czekanski.bet.domain.match

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.firebase.dynamiclinks.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import durdinapps.rxfirebase2.*
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.utils.ItemDecorator
import info.czekanski.bet.domain.match.BetViewState.Step.*
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.network.*
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.network.model.Bet
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_bet.*
import kotlinx.android.synthetic.main.layout_match_bid.*
import kotlinx.android.synthetic.main.layout_match_score.*

class BetFragment : Fragment() {
    private val betRepository by lazy { BetRepository.instance }
    private val matcheRepository by lazy { MatchRepository.instance }
    private val scopeProvider by lazy { AndroidLifecycleScopeProvider.from(this) }
    private val nicknameCache by lazy { NicknameCache.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val betService: BetService by lazy { BetService.instance }
    private val arg by lazy { getArgument<Argument>() }
    private val state: MutableLiveData<BetViewState> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        val (matchId, betId) = arg

        if (matchId == null && betId == null) {
            throw RuntimeException("Invalid parameters for MatchFragment - pass either matchId or betId")
        } else if (matchId != null) {
            loadMatch(matchId)
        } else if (betId != null) {
            loadBet(betId)
        }

        initViews()
        state.observe(this, Observer<BetViewState> { if (it != null) updateView(it) })

        state.setValue(BetViewState(step = BID))
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    private fun initViews() {
        buttonMinus.setOnClickListener {
            if (state.v.bid > 0) state.setValue(state.v.copy(bid = state.v.bid - 5))
        }
        buttonPlus.setOnClickListener {
            if (state.v.bid < 100) state.setValue(state.v.copy(bid = state.v.bid + 5))
        }
        buttonAccept.setOnClickListener {
            state.setValue(state.v.copy(step = SCORE))
        }

        buttonMinus1.setOnClickListener {
            if (state.v.score.first > 0) state.setValue(state.v.updateScore(first = state.v.score.first - 1))
        }
        buttonPlus1.setOnClickListener {
            if (state.v.score.first < 9) state.setValue(state.v.updateScore(first = state.v.score.first + 1))
        }

        buttonMinus2.setOnClickListener {
            if (state.v.score.second > 0) state.setValue(state.v.updateScore(second = state.v.score.second - 1))
        }
        buttonPlus2.setOnClickListener {
            if (state.v.score.second < 9) state.setValue(state.v.updateScore(second = state.v.score.second + 1))
        }
        buttonAccept2.setOnClickListener {
            val s = state.v
            if (s.match == null) return@setOnClickListener
            if (s.bet == null) {
                betService.api.createBet(s.match.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                        .doOnSubscribe { this.state.setValue(this.state.v.copy(step = LIST, showLoader = true)) }
                        .doFinally { this.state.setValue(this.state.v.copy(showLoader = false)) }
                        .applySchedulers()
                        .autoDisposable(scopeProvider)
                        .subscribeBy(onSuccess = { result ->
                            if (state.v.bet == null) {
                                loadBet(result.id)
                            }
                        }, onError = {
                            state.setValue(state.v.copy(step = BID))
                            Toast.makeText(context, "Unable to create bet!", Toast.LENGTH_SHORT).show()
                            Log.w("CreateBet", it)
                        })
            } else {
                betService.api.updateBet(s.bet.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                        .doOnSubscribe { this.state.setValue(this.state.v.copy(showLoader = true)) }
                        .doFinally { this.state.setValue(this.state.v.copy(showLoader = false)) }
                        .applySchedulers()
                        .autoDisposable(scopeProvider)
                        .subscribeBy(onError = {
                            Toast.makeText(context, "Unable to update bet!", Toast.LENGTH_SHORT).show()
                            Log.w("UpdateBet", it)
                        })
            }
        }

        buttonEdit.setOnClickListener {
            if (state.v.step == LIST) {
                state.setValue(state.v.copy(step = BID))
            }
        }

        recyclerView.addItemDecoration(ItemDecorator())
    }

    private fun loadMatch(matchId: String) {
        matcheRepository.observeMatch(matchId)
                .autoDisposable(scopeProvider)
                .subscribeBy(
                        onNext = { state.setValue(state.v.copy(match = it)) },
                        onError = {
                            Toast.makeText(context, "Unable to load match!", Toast.LENGTH_SHORT).show()
                            Log.e("MatchFragment", "getMatch", it)
                        }
                )
    }

    private fun loadBet(betId: String) {
        betRepository.observeBet(betId)
                .autoDisposable(scopeProvider)
                .subscribeBy(onNext = { bet ->
                    state.value = state.v.copy(step = LIST, bet = bet)
                    loadNicknames(bet)
                    if (state.v.match == null) loadMatch(bet.matchId)
                }, onError = {
                    Toast.makeText(context, "Unable to load bet!", Toast.LENGTH_SHORT).show()
                    Log.w("loadBet", it)
                })
    }

    private fun loadNicknames(bet: FirebaseBet) {
        val nicknames = state.v.nicknames

        var flowable: Flowable<Pair<String, String?>> = Flowable.empty()

        bet.bets.keys.forEach { userId ->
            if (!nicknames.containsKey(userId)) {
                if (nicknameCache.map.containsKey(userId)) {
                    state.setValue(state.v.updateNickname(userId, nicknameCache.map[userId]))
                } else {
                    flowable = flowable.mergeWith(loadNickname(userId).toFlowable())
                }
            }
        }

        flowable
                .autoDisposable(scopeProvider)
                .subscribeBy(onNext = {
                    val (userId, nick) = it
                    nicknameCache.map[userId] = nick
                    state.setValue(state.v.updateNickname(userId, nick))
                }, onError = {
                    Log.e("LoadNicknames", "User ...", it)
                })

    }

    private fun loadNickname(userId: String): Maybe<Pair<String, String?>> {
        return RxFirestore.getDocument(firestore.collection("users").document(userId))
                .map { Pair(userId, it.getString("nick")) }
                .applySchedulers()
    }


    private fun updateView(state: BetViewState) {
        // Misc
        if (state.match == null) {
            viewMatch.invisible()
        } else {
            viewMatch.show()
            viewMatch.bindMatch(state.match)
        }
        imageBall.show(state.step != LIST)
        buttonEdit.show(state.step == LIST && state.bet != null)
        progress.show(state.showLoader)

        // Steps
        layoutBid.show(state.step == BID)
        layoutScore.show(state.step == SCORE)
        recyclerView.show(state.step == LIST)

        when (state.step) {
            BID -> {
                textBid.text = "${state.bid} zł"
            }
            SCORE -> {
                textScore1.text = "${state.score.first}"
                textScore2.text = "${state.score.second}"
            }
            LIST -> {
                val cells: MutableList<Cell>
                if (state.bet == null) {
                    cells = mutableListOf()
                } else {
                    cells = mutableListOf(
                            HeaderCell(),
                            SeparatorCell()
                    )

                    state.bet.bets.forEach {
                        val userId = it.key
                        val betEntry = it.value
                        val score = betEntry.score.scoreToPair() ?: return@forEach

                        cells += EntryCell(state.nicknames.getOrDefault(userId, ". . ."), score)
                    }

                    // Get user id and find his bet
                    val stake = state.bet.bets[userProvider.userId]?.bid ?: 0

                    val jackpot = state.bet.bets.values
                            .mapNotNull { it.bid }
                            .reduce { acc, i -> acc + i }

                    cells += SeparatorCell()
                    cells += SummaryCell(stake, jackpot)
                    cells += InviteCell(showText = state.bet.bets.size < 2)
                }
                recyclerView.adapter = SummaryAdapter(cells, {
                    when (it) {
                        is InviteCell -> {
                            createShareLink()
                                    .doOnSubscribe { this.state.setValue(this.state.v.copy(showLoader = true)) }
                                    .doFinally { this.state.setValue(this.state.v.copy(showLoader = false)) }
                                    .subscribeBy(onSuccess = {
                                        openShareWindow(it.shortLink)
                                    }, onError = {
                                        Log.e("MatchFragment", "createShareLink", it)
                                    })
                        }
                    }
                })
            }
        }
    }

    fun createShareLink(): Maybe<ShortDynamicLink> {
        val betId = state.v.bet?.id ?: return Maybe.error(RuntimeException("No bet id!"))

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://bet.czekanski.info/bet/$betId"))
                .setDynamicLinkDomain("bet.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Załóż się")
                        .setDescription("Pobierz aplikacje i dołącz do zabawy")
                        .build())
                .buildShortDynamicLink()

        return Maybe.create<ShortDynamicLink> { emitter -> RxHandler.assignOnTask(emitter, dynamicLink) }
                .applySchedulers()
    }

    private fun openShareWindow(link: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, link.toString())
        intent.type = "text/plain"
        activity?.startActivity(Intent.createChooser(intent, "Udostępnij"))
    }

    @Parcelize
    data class Argument(val matchId: String? = null, val betId: String? = null) : Parcelable
}