package info.czekanski.bet.domain.home

import android.arch.lifecycle.*
import android.util.Log
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.*

class HomeViewModel : ViewModel() {
    val betRepository by lazy { BetRepository.instance }
    val matchesRepository by lazy { MatchRepository.instance }
    val userProvider by lazy { UserProvider.instance }
    private val liveCells = MutableLiveData<List<Cell>>()
    private var subscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
        subscription = null
    }

    private fun loadData() {
        val betsFlowable = betRepository.getBets()
        val matchesFlowable = matchesRepository.getMatches()
        val userNameFlowable = userProvider.loadNick().toFlowable()

        subscription = Flowables.combineLatest(betsFlowable, matchesFlowable, userNameFlowable, { b, m, _ -> Pair(b, m) })
                .doOnSubscribe {
                    liveCells.value = listOf(LoaderCell())
                }
                .subscribeBy(onNext = {
                    val (bets, matches) = it
                    val cells = mutableListOf<Cell>(WelcomeCell(userProvider.nick))

                    if (bets.isNotEmpty()) {
                        val betsWithMatches = bets.map { bet ->
                            bet.copy(match = matches.find { match ->
                                match.id == bet.matchId
                            })
                        }.sortedBy { it.match?.date }
                        cells += HeaderCell("Twoje typy")
                        cells += betsWithMatches.map { BetCell(it) }
                    }

                    if (matches.isNotEmpty()) {
                        cells += HeaderCell("Najbliższe mecze")
                        cells += matches
                                .take(4)
                                .filter { it.state != MatchState.AFTER }
                                .map { MatchCell(it) }
                    }

                    liveCells.value = cells
                }, onError = {
                    Log.e("HomeFragment", "LoadMatchesAndBets", it)
                })
    }

    fun getCells(): LiveData<List<Cell>> {
        if (subscription == null) {
            loadData()
        }
        return liveCells
    }
}
