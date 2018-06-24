package info.czekanski.bet.di.utils

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.android.AndroidInjector

interface HasViewHolderInjector {
    fun viewHolderInjector(): AndroidInjector<RecyclerView.ViewHolder>
}