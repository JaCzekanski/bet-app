package info.czekanski.bet.domain.bets

import info.czekanski.bet.domain.base.BaseHomeViewModel
import info.czekanski.bet.domain.home.HomeViewModel
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.repository.*
import io.reactivex.rxkotlin.*
import timber.log.Timber
import javax.inject.Inject

class BetsViewModel @Inject constructor(
        private val betsRepository: BetRepository,
        private val matchesRepository: MatchRepository
) : BaseHomeViewModel() {
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
                    Timber.e(it, "loadData")
                })
    }
}
