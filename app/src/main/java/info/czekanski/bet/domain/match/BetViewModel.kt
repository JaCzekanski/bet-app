package info.czekanski.bet.domain.match

import android.arch.lifecycle.*
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
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
    private val betService: BetService by lazy { BetService.instance }
    private val betRepository by lazy { BetRepository.instance }
    private val matchRepository by lazy { MatchRepository.instance }
    private val subs = CompositeDisposable()
    private val nicknameCache by lazy { NicknameCache.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val state: MutableLiveData<BetViewState> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        subs.clear()
    }

    fun buttonClicked(action: Action) {
        when (action) {
            Action.BidPlus -> {
                if (state.v.bid > 0) state.value = state.v.copy(bid = state.v.bid - 5)
            }
            Action.BidMinus -> {
                if (state.v.bid < 100) state.value = state.v.copy(bid = state.v.bid + 5)
            }
            Action.BidAccept -> {
                state.setValue(state.v.copy(step = BetViewState.Step.SCORE))
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
        }
    }

    private fun updateOrCreateBet() {
        val s = state.v
        if (s.match == null) return
        if (s.bet == null) {
            subs += betService.api.createBet(s.match.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                    .doOnSubscribe { this.state.setValue(this.state.v.copy(step = BetViewState.Step.LIST, showLoader = true)) }
                    .doFinally { this.state.setValue(this.state.v.copy(showLoader = false)) }
                    .applySchedulers()
                    .subscribeBy(onSuccess = { result ->
                        if (state.v.bet == null) {
                            loadBet(result.id)
                        }
                    }, onError = {
                        state.setValue(state.v.copy(step = BetViewState.Step.BID))
//                        Toast.makeText(context, "Unable to create bet!", Toast.LENGTH_SHORT).show()
                        Log.w("CreateBet", it)
                    })
        } else {
            subs += betService.api.updateBet(s.bet.id, Bet(state.v.bid, state.v.scoreAsString()), userProvider.userId!!)
                    .doOnSubscribe { this.state.setValue(this.state.v.copy(showLoader = true)) }
                    .doFinally { this.state.setValue(this.state.v.copy(showLoader = false)) }
                    .applySchedulers()
                    .subscribeBy(onError = {
                        //                        Toast.makeText(context, "Unable to update bet!", Toast.LENGTH_SHORT).show()
                        Log.w("UpdateBet", it)
                    })
        }
    }


    private fun loadMatch(matchId: String) {
        subs += matchRepository.observeMatch(matchId)
                .subscribeBy(
                        onNext = { state.setValue(state.v.copy(match = it)) },
                        onError = {
                            //                            Toast.makeText(context, "Unable to load match!", Toast.LENGTH_SHORT).show()
                            Log.e("MatchFragment", "getMatch", it)
                        }
                )
    }

    private fun loadBet(betId: String) {
        subs += betRepository.observeBet(betId)
                .subscribeBy(onNext = { bet ->
                    state.value = state.v.copy(step = BetViewState.Step.LIST, bet = bet)
                    loadNicknames(bet)
                    if (state.v.match == null) loadMatch(bet.matchId)
                }, onError = {
                    //                    Toast.makeText(context, "Unable to load bet!", Toast.LENGTH_SHORT).show()
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

        subs += flowable
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

    enum class Action {
        BidMinus, BidPlus, BidAccept,
        Team1ScoreMinus, Team1ScorePlus, Team2ScoreMinus, Team2ScorePlus, ScoreAccept,
        EditBet
    }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}