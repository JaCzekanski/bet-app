package info.czekanski.bet.di.component

import dagger.*
import dagger.android.AndroidInjector
import info.czekanski.bet.MyApplication
import info.czekanski.bet.di.module.*
import info.czekanski.bet.di.module.domain.*
import info.czekanski.bet.di.utils.CustomInjectionModule
import info.czekanski.bet.di.module.view_model.ViewModelModule
import info.czekanski.bet.views.OctagonalImageView
import javax.inject.Singleton

@Component(modules = [
    CustomInjectionModule::class,
    ContextModule::class,
    ServiceModule::class,
    ViewModelModule::class,
    ActivityModule::class,
    FragmentModule::class
])
@Singleton
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: MyApplication): Builder
        fun build(): AppComponent
    }

    fun inject(octagonalImageView: OctagonalImageView)
}