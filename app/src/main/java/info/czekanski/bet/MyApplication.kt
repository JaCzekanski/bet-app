package info.czekanski.bet

import android.app.Application
import info.czekanski.bet.service.NotificationService

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationService.createNotificationChannels(this)
    }
}