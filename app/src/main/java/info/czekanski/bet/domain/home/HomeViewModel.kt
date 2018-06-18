package info.czekanski.bet.domain.home

import android.util.Log
import info.czekanski.bet.domain.base.BaseHomeViewModel
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.model.*
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.*
import timber.log.Timber

class HomeViewModel : BaseHomeViewModel() {
    private val betRepository by lazy { BetRepository.instance }
    private val matchesRepository by lazy { MatchRepository.instance }
    private val userProvider by lazy { UserProvider.instance }
    private val preferencesProvider by lazy { PreferencesProvider._instance!! } // TODO: Kill it with fire!!!!!

    override fun loadData() {
        val betsFlowable = betRepository.getBets().retry()
        val matchesFlowable = matchesRepository.getMatches().retry()
        val userNameFlowable = userProvider.loadNick().toFlowable().retry()

        subscription = Flowables.combineLatest(betsFlowable, matchesFlowable, userNameFlowable, { bets, matches, _ -> Pair(mergeMatchesIntoBets(bets, matches), matches) })
                .doOnSubscribe { liveCells.value = listOf(LoaderCell()) }
                .subscribeBy(onNext = {
                    val (bets, matches) = it

                    val welcomeCell = WelcomeCell(userProvider.nick, preferencesProvider.runCount < 3)
                    val cells = listOf<Cell>(welcomeCell) +
                            createResultsCell(bets) +
                            createBetCells(bets) +
                            createMatchCells(matches)

                    liveCells.value = cells
                }, onError = {
                    Timber.e(it, "loadData")
                })
    }

    private fun createResultsCell(bets: List<FirebaseBet>): List<Cell> {
        val bettedCount = bets.filter { it.bets[userProvider.userId!!]?.bid != null }.count()
        val wonCount = bets.filter { it.bets[userProvider.userId!!]?.score == it.match?.score }.count()

        if (bettedCount == 0) return listOf()

        return listOf(ResultsCell(bettedCount, wonCount))
    }

    private fun createBetCells(bets: List<FirebaseBet>): List<Cell> {
        val filteredBets = bets
                .filter { it.match?.state != MatchState.AFTER }
                .sortedBy { it.match?.date }
                .map { BetCell(it) }

        if (filteredBets.isEmpty()) {
            return listOf()
        }

        return listOf(HeaderCell("Aktualne typy")) + filteredBets
    }

    private fun createMatchCells(matches: List<Match>): List<Cell> {
        val filteredMatches = matches
                .filter { it.state != MatchState.AFTER }
                .take(4)
                .map { MatchCell(it) }

        if (filteredMatches.isEmpty()) {
            return listOf()
        }

        return listOf(HeaderCell("Najbliższe mecze")) + filteredMatches
    }

    companion object {
        fun mergeMatchesIntoBets(bets: List<FirebaseBet>, matches: List<Match>) =
                bets.map { bet ->
                    bet.copy(match = matches.find { match ->
                        match.id == bet.matchId
                    })
                }
    }
}
