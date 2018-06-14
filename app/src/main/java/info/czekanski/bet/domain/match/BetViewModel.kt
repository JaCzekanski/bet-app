package info.czekanski.bet.domain.match

import android.arch.lifecycle.*
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.*
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.network.BetService
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.network.model.Bet
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.rxkotlin.subscribeBy

class BetViewModel : ViewModel() {
    private val subs = CompositeDisposable()
    private val betService: BetService by lazy { BetService.instance }
    private val betRepository by lazy { BetRepository.instance }
    private val matchRepository by lazy { MatchRepository.instance }
    private val nicknameCache by lazy { NicknameCache.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val state = MutableLiveData<BetViewState>()
    private val shareLink = MutableLiveData<ShortDynamicLink>()
    private val toast = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
        subs.clear()
    }

    fun getState(arg: BetFragment.Argument): LiveData<BetViewState> {
        if (state.value == null) {
            val (matchId, betId) = arg

            if (matchId == null && betId == null) {
                throw RuntimeException("Invalid parameters for MatchFragment - pass either matchId or betId")
            } else if (matchId != null) {
                loadMatch(matchId)
            } else if (betId != null) {
                loadBet(betId)
            }

            state.value = BetViewState(step = BetViewState.Step.BID)
        }

        return state
    }

    fun getShareLink(): LiveData<ShortDynamicLink> {
        shareLink.value = null
        return shareLink
    }

    fun getToast(): LiveData<String> {
        toast.value = null
        return toast
    }


    fun buttonClicked(action: Action) {
        when (action) {
            Action.BidMinus -> {
                if (state.v.bid > 0) state.value = state.v.copy(bid = state.v.bid - 5)
            }
            Action.BidPlus -> {
                if (state.v.bid < 100) state.value = state.v.copy(bid = state.v.bid + 5)
            }
            Action.BidAccept -> {
                state.value = state.v.copy(step = BetViewState.Step.SCORE)
            }
            Action.Team1ScoreMinus -> {
                if (state.v.score.first > 0) state.value = state.v.updateScore(first = state.v.score.first - 1)
            }
            Action.Team1ScorePlus -> {
                if (state.v.score.first < 9) state.value = state.v.updateScore(first = state.v.score.first + 1)
            }
            Action.Team2ScoreMinus -> {
                if (state.v.score.second > 0) state.value = state.v.updateScore(second = state.v.score.second - 1)
            }
            Action.Team2ScorePlus -> {
                if (state.v.score.second < 9) state.value = state.v.updateScore(second = state.v.score.second + 1)
            }
            Action.ScoreAccept -> {
                updateOrCreateBet()
            }
            Action.EditBet -> {
                if (state.v.step == BetViewState.Step.LIST) {
                    state.value = state.v.copy(step = BetViewState.Step.BID)
                }
            }
            Action.Share -> {
                createShareLink()
                        .doOnSubscribe { state.value = state.v.copy(showLoader = true) }
                        .doFinally { state.value = state.v.copy(showLoader = false) }
                        .subscribeBy(
                                onSuccess = { shareLink.value = it },
                                onError = { Log.e("MatchFragment", "createShareLink", it) }
                        )
            }
        }
    }

    private fun updateOrCreateBet() {
        val s = state.v
        if (s.match == null) return
        if (s.bet == null) {
            subs += betService.api.createBet(s.match.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                    .applySchedulers()
                    .doOnSubscribe { state.value = this.state.v.copy(showLoader = true) }
                    .doFinally { state.value = this.state.v.copy(step = BetViewState.Step.LIST, showLoader = false) }
                    .subscribeBy(onSuccess = { result ->
                        if (state.v.bet == null) {
                            loadBet(result.id)
                        }
                    }, onError = {
                        state.value = state.v.copy(step = BetViewState.Step.BID)
                        toast.value = "Unable to create bet!"
                        Log.w("CreateBet", it)
                    })
        } else {
            subs += betService.api.updateBet(s.bet.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                    .applySchedulers()
                    .doOnSubscribe { state.value = this.state.v.copy(showLoader = true) }
                    .doFinally { state.value = this.state.v.copy(step = BetViewState.Step.LIST, showLoader = false) }
                    .subscribeBy(onError = {
                        toast.value = "Unable to update bet!"
                        Log.w("UpdateBet", it)
                    })
        }
    }


    private fun loadMatch(matchId: String) {
        subs += matchRepository.observeMatch(matchId)
                .subscribeBy(
                        onNext = { state.value = state.v.copy(match = it) },
                        onError = {
                            toast.value = "Unable to load match!"
                            Log.e("MatchFragment", "getMatch", it)
                        }
                )
    }

    private fun loadBet(betId: String) {
        subs += betRepository.observeBet(betId)
                .subscribeBy(onNext = { bet ->
                    state.value = state.v.copy(bet = bet)

                    if (bet.bets.containsKey(userProvider.userId)) {
                        state.value = state.v.copy(step = BetViewState.Step.LIST)
                    }

                    loadNicknames(bet)
                    if (state.v.match == null) loadMatch(bet.matchId)
                }, onError = {
                    toast.value = "Unable to load bet!"
                    Log.w("loadBet", it)
                })
    }

    private fun loadNicknames(bet: FirebaseBet) {
        val nicknames = state.v.nicknames

        var flowable: Flowable<Pair<String, String?>> = Flowable.empty()

        bet.bets.keys.forEach { userId ->
            if (!nicknames.containsKey(userId)) {
                if (nicknameCache.map.containsKey(userId)) {
                    state.value = state.v.updateNickname(userId, nicknameCache.map[userId])
                } else {
                    flowable = flowable.mergeWith(loadNickname(userId).toFlowable())
                }
            }
        }

        subs += flowable
                .subscribeBy(onNext = {
                    val (userId, nick) = it
                    nicknameCache.map[userId] = nick
                    state.value = state.v.updateNickname(userId, nick)
                }, onError = {
                    Log.e("LoadNicknames", "User ...", it)
                })

    }

    private fun loadNickname(userId: String): Maybe<Pair<String, String?>> {
        return RxFirestore.getDocument(firestore.collection("users").document(userId))
                .map { Pair(userId, it.getString("nick")) }
                .applySchedulers()
    }


    private fun createShareLink(): Maybe<ShortDynamicLink> {
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

    enum class Action {
        BidMinus, BidPlus, BidAccept,
        Team1ScoreMinus, Team1ScorePlus, Team2ScoreMinus, Team2ScorePlus, ScoreAccept,
        EditBet, Share
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}