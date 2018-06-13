package info.czekanski.bet.misc

import android.arch.lifecycle.*


inline val <T : Any> MutableLiveData<T>.v
    get() = value!!


inline fun <T> LiveData<T>.safeObserve(lifecycle: LifecycleOwner, crossinline callback: (T) -> Unit) {
    observe(lifecycle, Observer {
        if (it != null) callback(it)
    })
}