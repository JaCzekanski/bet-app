package info.czekanski.bet

import android.app.Application
import info.czekanski.bet.service.NotificationService
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationService.createNotificationChannels(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}