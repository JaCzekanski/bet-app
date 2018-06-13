package info.czekanski.bet.misc

import android.arch.lifecycle.*


val <T : Any> MutableLiveData<T>.v
    get() = value!!

