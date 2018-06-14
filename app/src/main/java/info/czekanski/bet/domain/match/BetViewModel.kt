package info.czekanski.bet.domain.match

import android.arch.lifecycle.*
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.*
import durdinapps.rxfirebase2.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.network.BetService
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.network.model.Bet
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.*
import io.reactivex.disposables.*
import io.reactivex.rxkotlin.*

class BetViewModel : ViewModel() {
    private val subs = CompositeDisposable()
    private val betService: BetService by lazy { BetService.instance }
    private val betRepository by lazy { BetRepository.instance }
    private val matchRepository by lazy { MatchRepository.instance }
    private val friendsRepository by lazy { FriendsRepository.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val state = MutableLiveData<BetViewState>()
    private val toast = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
        subs.clear()
    }

    fun getState(arg: BetFragment.Argument? = null): LiveData<BetViewState> {
        if (state.value == null && arg != null) {
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
            Action.DeleteBet -> {
                if (state.v.step == BetViewState.Step.LIST && state.v.bet != null) {
                    deleteBet()
                }
            }
            Action.Share -> action@ {
                val userId = userProvider.userId ?: return@action

                val friendsSingle = friendsRepository.getFriends(userId)
                        .map {friends -> // Filter out friends that are already invited
                            friends.filter {state.v.bet?.users?.containsKey(it.id) == false}
                        }

                Singles.zip(friendsSingle, createShareLink().toSingle(), { friends, link -> Pair(friends, link) })
                        .doOnSubscribe { state.value = state.v.copy(showLoader = true) }
                        .doFinally { state.value = state.v.copy(showLoader = false) }
                        .subscribeBy(onSuccess = {
                            val (friends, link) = it

                            state.value = state.v.copy(
                                    friends = friends,
                                    shareLink = link,
                                    step = BetViewState.Step.FRIENDS
                            )
                        }, onError = {
                            Log.e("MatchFragment", "createShareLink", it)
                        })
            }
        }
    }

    private fun deleteBet() {
        val s = state.v
        if (s.bet != null) {
            subs += betService.api.deleteBet(s.bet.id, userProvider.userId!!)
                    .applySchedulers()
                    .doOnSubscribe { state.value = this.state.v.copy(showLoader = true) }
                    .doFinally { state.value = this.state.v.copy(showLoader = false) }
                    .subscribeBy(onComplete = {
                        state.value = this.state.v.copy(closeView = true)
                    }, onError = {
                        toast.value = "Unable to delete bet!"
                        Log.w("DeleteBet", it)
                    })
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
        var flowable: Flowable<Friend> = Flowable.empty()

        bet.bets.keys.forEach { userId ->
            flowable = flowable.mergeWith(friendsRepository.getName(userId)
                    .map { userName -> Friend(userId, userName) }
                    .toFlowable())
        }

        subs += flowable
                .subscribeBy(onNext = { friend ->
                    val (userId, nick) = friend
                    state.value = state.v.updateNickname(userId, nick)
                }, onError = {
                    Log.e("LoadNicknames", "User ...", it)
                })

    }

    // TODO: To provider
    private fun createShareLink(): Maybe<Uri> {
        val betId = state.v.bet?.id ?: return Maybe.error(RuntimeException("No bet id!"))

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://bet.czekanski.info/bet/$betId"))
                .setDynamicLinkDomain("uzz4b.app.goo.gl")
//                .setDynamicLinkDomain("bet.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Załóż się kto wygra")
                        .setDescription("Pobierz aplikacje i dołącz do zabawy")
                        .setImageUrl(Uri.parse("https://i.imgur.com/GwVqwJ0.png"))
                        .build())
                .buildShortDynamicLink()

        return Maybe.create<ShortDynamicLink> { emitter -> RxHandler.assignOnTask(emitter, dynamicLink) }
                .map { it.shortLink }
                .applySchedulers()
    }

    fun shareLinkTo(userId: String) {
        val betId = state.v.bet?.id ?: return
        betService.api.inviteUser(betId, userId, userProvider.userId!!)
                .applySchedulers()
                .doOnSubscribe { state.value = this.state.v.copy(showLoader = true) }
                .doFinally { state.value = this.state.v.copy(showLoader = false) }
                .subscribeBy(onError = {
                    toast.value = "Unable to share bet!"
                    Log.w("shareLinkTo", it)
                })
    }

    fun sharedLink() {
        state.value = state.v.copy(step = BetViewState.Step.LIST)
    }

    enum class Action {
        BidMinus, BidPlus, BidAccept,
        Team1ScoreMinus, Team1ScorePlus, Team2ScoreMinus, Team2ScorePlus, ScoreAccept,
        DeleteBet, EditBet, Share,
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}