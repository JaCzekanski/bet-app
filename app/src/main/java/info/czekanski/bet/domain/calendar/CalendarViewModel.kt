package info.czekanski.bet.domain.calendar

import android.arch.lifecycle.*
import android.util.Log
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.*

class CalendarViewModel : ViewModel() {
    val matchesRepository by lazy { MatchRepository.instance }
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
