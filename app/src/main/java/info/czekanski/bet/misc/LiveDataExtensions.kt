package info.czekanski.bet.misc

import android.arch.lifecycle.*


inline val <T : Any> MutableLiveData<T>.v
    get() = value!!

