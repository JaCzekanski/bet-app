package info.czekanski.bet.domain.bets

import android.util.Log
import info.czekanski.bet.domain.base.BaseHomeViewModel
import info.czekanski.bet.domain.home.HomeViewModel
import info.czekanski.bet.domain.home.HomeViewModel.Companion.mergeMatchesIntoBets
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.repository.*
import io.reactivex.rxkotlin.*

class BetsViewModel : BaseHomeViewModel() {
    private val betsRepository by lazy { BetRepository.instance }
    private val matchesRepository by lazy { MatchRepository.instance }

    override fun loadData() {
        val betsFlowable = betsRepository.getBets()
        val matchesFlowable = matchesRepository.getMatches()

        subscription = Flowables.combineLatest(betsFlowable, matchesFlowable)
                .doOnSubscribe { liveCells.value = listOf(LoaderCell()) }
                .subscribeBy(onNext = {
                    val (bets, matches) = it
                    val cells = mutableListOf<Cell>()

                    if (bets.isNotEmpty()) {
                        cells += HeaderCell("Lista typów")
                        cells += HomeViewModel.mergeMatchesIntoBets(bets, matches)
                                .sortedBy { it.match?.date }
                                .map { BetCell(it) }
                    } else {
                        cells += EmptyCell.create()
                    }
                    liveCells.value = cells
                }, onError = {
                    Log.e("BetsViewModel", "loadData", it)
                })
    }
}
