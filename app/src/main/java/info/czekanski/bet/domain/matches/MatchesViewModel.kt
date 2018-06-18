package info.czekanski.bet.domain.matches

import info.czekanski.bet.domain.base.BaseHomeViewModel
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.repository.MatchRepository
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class MatchesViewModel : BaseHomeViewModel() {
    private val matchesRepository by lazy { MatchRepository.instance }

    override fun loadData() {
        subscription = matchesRepository.getMatches()
                .doOnSubscribe { liveCells.value = listOf(LoaderCell()) }
                .subscribeBy(onNext = { matches ->
                    val cells = mutableListOf<Cell>()
                    if (matches.isNotEmpty()) {
                        cells += HeaderCell("Lista meczy")
                        cells += matches.map { MatchCell(it) }
                    } else {
                        cells += EmptyCell.create()
                    }
                    liveCells.value = cells
                }, onError = {
                    Timber.e(it, "loadData")
                })
    }
}
