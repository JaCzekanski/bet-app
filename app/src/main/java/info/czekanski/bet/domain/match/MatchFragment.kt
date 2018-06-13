package info.czekanski.bet.domain.match

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.firestore.FirebaseFirestore
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import durdinapps.rxfirebase2.RxFirestore
import durdinapps.rxfirebase2.RxHandler
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.utils.ItemDecorator
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder.Companion.getCountryName
import info.czekanski.bet.domain.match.MatchViewState.Step.*
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.misc.GlideApp
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.misc.subscribeBy
import info.czekanski.bet.model.Match
import info.czekanski.bet.network.BetService
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.network.model.Bet
import info.czekanski.bet.network.scoreToPair
import info.czekanski.bet.user.UserProvider
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.android.synthetic.main.holder_summary_entry.*
import kotlinx.android.synthetic.main.layout_match.*
import kotlinx.android.synthetic.main.layout_match_bid.*
import kotlinx.android.synthetic.main.layout_match_score.*

class MatchFragment : Fragment() {
    private val userProvider by lazy { UserProvider.instance }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val betService: BetService by lazy { BetService.instance }

    private val arg by lazy { getArgument<Argument>() }

    private val state: MutableLiveData<MatchViewState> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match, container, false)
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
        state.observe(this, Observer<MatchViewState> { if (it != null) updateView(it) })

        state.postValue(MatchViewState(step = BID))
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    private fun initViews() {
        buttonMinus.setOnClickListener {
            if (state.v.bid > 0) state.postValue(state.v.copy(bid = state.v.bid - 5))
        }
        buttonPlus.setOnClickListener {
            if (state.v.bid < 100) state.postValue(state.v.copy(bid = state.v.bid + 5))
        }
        buttonAccept.setOnClickListener {
            state.postValue(state.v.copy(step = SCORE))
        }

        buttonMinus1.setOnClickListener {
            if (state.v.score.first > 0) state.postValue(state.v.updateScore(first = state.v.score.first - 1))
        }
        buttonPlus1.setOnClickListener {
            if (state.v.score.first < 9) state.postValue(state.v.updateScore(first = state.v.score.first + 1))
        }

        buttonMinus2.setOnClickListener {
            if (state.v.score.second > 0) state.postValue(state.v.updateScore(second = state.v.score.second - 1))
        }
        buttonPlus2.setOnClickListener {
            if (state.v.score.second < 9) state.postValue(state.v.updateScore(second = state.v.score.second + 1))
        }
        buttonAccept2.setOnClickListener {
            val s = state.v
            if (s.match == null) return@setOnClickListener
            if (s.bet == null) {
                betService.api.createBet(s.match.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { state.postValue(state.v.copy(step = LIST)) }
                        .subscribeBy(onSuccess = { result ->
                            if (state.v.bet == null) {
                                loadBet(result.id)
                            }
                        }, onError = {
                            state.postValue(state.v.copy(step = BID))
                            Toast.makeText(context, "Unable to create bet!", Toast.LENGTH_SHORT).show()
                            Log.w("CreateBet", it)
                        })
            } else {
                betService.api.updateBet(s.bet.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(onError = {
                            Toast.makeText(context, "Unable to update bet!", Toast.LENGTH_SHORT).show()
                            Log.w("UpdateBet", it)
                        })
            }
        }

        buttonEdit.setOnClickListener {
            if (state.v.step == LIST) {
                state.postValue(state.v.copy(step = BID))
            }
        }

        recyclerView.addItemDecoration(ItemDecorator())
    }

    private fun loadMatch(matchId: String) {
        RxFirestore.observeDocumentRef(firestore.collection("matches").document(matchId))
                .filter { it.exists() }
                .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                .applySchedulers()
                .autoDisposable(AndroidLifecycleScopeProvider.from(this))
                .subscribeBy(
                        onNext = { state.postValue(state.v.copy(match = it)) },
                        onError = { Log.e("MatchFragment", "getMatch", it) }
                )
    }

    private fun loadBet(id: String) {
        RxFirestore.observeDocumentRef(firestore.collection("bets").document(id))
                .applySchedulers()
                .autoDisposable(AndroidLifecycleScopeProvider.from(this))
                .subscribeBy(onNext = { doc ->
                    if (doc == null || !doc.exists()) {
                        state.postValue(state.v.copy(step = SCORE))
                        Toast.makeText(context, "Unable to load bet! wtf", Toast.LENGTH_SHORT).show()
                        return@subscribeBy
                    }

                    val bet = doc.toObject(FirebaseBet::class.java)!!.copy(id = doc.id)
                    state.postValue(state.v.copy(step = LIST, bet = bet))

                    if (state.v.match == null) {
                        loadMatch(bet.matchId)
                    }
                }, onError = {
                    Toast.makeText(context, "Unable to load bet!", Toast.LENGTH_SHORT).show()
                    Log.w("loadBet", it)
                })
    }

    private fun updateView(state: MatchViewState) {
        // Misc
        if (state.match == null) {
            layoutMatch.visibility = View.INVISIBLE
        } else {
            bindMatch(state.match)
        }
        imageBall.show(state.step != LIST)
        buttonEdit.show(state.step == LIST && state.bet != null)

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
                    cells = mutableListOf(LoaderCell())
                } else {
                    cells = mutableListOf(
                            HeaderCell(),
                            SeparatorCell()
                    )

                    state.bet.bets.forEach {
                        val userId = it.key
                        val betEntry = it.value

                        val score = betEntry.score.scoreToPair() ?: return@forEach
                        cells += EntryCell(userId, score)
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
                            createShareLink().subscribeBy(onSuccess = {
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

    private fun bindMatch(match: Match) {
        layoutMatch.show()

        val gameScore = match.score?.scoreToPair() ?: Pair(0, 0)

        score.setTextColor(ContextCompat.getColor(requireContext(), if (match.score != null) R.color.textActive else R.color.textInactive))
        score.text = "%d - %d".format(gameScore.first, gameScore.second)

        date.text = MatchViewHolder.formatter.format(match.date)
        team1.text = getCountryName(match.team1)
        team2.text = getCountryName(match.team2)

        GlideApp.with(flag1.context)
                .load(Uri.parse("file:///android_asset/flags/${match.team1}.png"))
                .centerInside()
                .into(flag1)

        GlideApp.with(flag2.context)
                .load(Uri.parse("file:///android_asset/flags/${match.team2}.png"))
                .centerInside()
                .apply(RequestOptions.circleCropTransform())
                .into(flag2)

        button.visibility = INVISIBLE
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


fun <T : Fragment> T.withArgument(arg: Parcelable): T {
    val bundle = Bundle()
    bundle.putParcelable("ARG", arg)
    arguments = bundle
    return this
}


fun <T : Parcelable> Fragment.getArgument(): T =
        arguments?.getParcelable("ARG")!!


fun View.hide() {
    visibility = GONE
}

fun View.show(visible: Boolean = true) {
    visibility = if (visible) VISIBLE else GONE
}

val <T : Any> MutableLiveData<T>.v
    get() = value!!

