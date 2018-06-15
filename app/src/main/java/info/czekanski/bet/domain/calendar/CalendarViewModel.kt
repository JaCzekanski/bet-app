package info.czekanski.bet.domain.calendar

import android.arch.lifecycle.*
import android.util.Log
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.repository.MatchRepository
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class CalendarViewModel : ViewModel() {
    private val matchesRepository by lazy { MatchRepository.instance }
    private val liveCells = MutableLiveData<List<Cell>>()
    private var subscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
        subscription = null
    }

    private fun loadData() {
        val matchesFlowable = matchesRepository.getMatches()

        subscription = matchesFlowable
                .doOnSubscribe {
                    liveCells.value = listOf(LoaderCell())
                }
                .subscribeBy(onNext = { matches ->
                    val cells = mutableListOf<Cell>()
                    if (matches.isNotEmpty()) {
                        cells += HeaderCell("Lista meczy")
                        cells += matches.map { MatchCell(it) }
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
