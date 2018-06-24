package info.czekanski.bet.di.utils

import android.app.Fragment
import android.view.View
import dagger.android.AndroidInjector

interface HasViewInjector {
    fun viewInjector(): AndroidInjector<View>
}