package info.czekanski.bet.di.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.android.*
import dagger.android.support.DaggerApplication
import javax.inject.Inject

abstract class CustomDaggerApplication: DaggerApplication(), HasViewInjector, HasViewHolderInjector {
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>
    @Inject lateinit var viewHolderInjector: DispatchingAndroidInjector<RecyclerView.ViewHolder>

    override fun viewInjector(): AndroidInjector<View> = viewInjector
    override fun viewHolderInjector(): AndroidInjector<RecyclerView.ViewHolder> = viewHolderInjector
}