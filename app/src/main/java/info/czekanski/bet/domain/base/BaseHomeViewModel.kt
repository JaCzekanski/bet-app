package info.czekanski.bet.domain.base

import android.arch.lifecycle.*
import android.util.Log
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.*

abstract class BaseHomeViewModel : ViewModel() {
    protected val liveCells = MutableLiveData<List<Cell>>()
    protected var subscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
        subscription = null
    }

    protected abstract fun loadData()

    fun getCells(): LiveData<List<Cell>> {
        if (subscription == null) {
            loadData()
        }
        return liveCells
    }
}
