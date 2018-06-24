package info.czekanski.bet.di.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.internal.Beta
import dagger.multibindings.Multibinds

@Beta
@Module(includes = [(AndroidSupportInjectionModule::class)])
abstract class CustomInjectionModule private constructor() {
    @Multibinds
    abstract fun viewInjectorFactories(): Map<Class<out View>, AndroidInjector.Factory<out View>>

    @Multibinds
    abstract fun viewHolderInjectorFactories(): Map<Class<out RecyclerView.ViewHolder>, AndroidInjector.Factory<out RecyclerView.ViewHolder>>
}
