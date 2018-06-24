package info.czekanski.bet

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import info.czekanski.bet.di.*
import info.czekanski.bet.di.component.*
import info.czekanski.bet.di.utils.CustomDaggerApplication
import info.czekanski.bet.service.NotificationService
import timber.log.Timber

class MyApplication : CustomDaggerApplication() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        NotificationService.createNotificationChannels(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val c = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        this.component = c

        return c
    }

    companion object {
        private var instance: MyApplication? = null

        fun get(): MyApplication = instance!!
    }
}