package info.czekanski.bet.service

import com.google.firebase.iid.*
import dagger.android.AndroidInjection
import info.czekanski.bet.MyApplication
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class FirebaseTokenRegistrationService : FirebaseInstanceIdService() {
    @Inject lateinit var userProvider: UserProvider

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Timber.d("onTokenRefresh: $token")

        if (userProvider.loggedIn && token != null) {
            userProvider.setFcmToken(token)
                    .subscribeBy(onComplete = {
                        Timber.d("Device registered")
                    }, onError = {
                        Timber.e(it,  "Unable to register device")
                    })
        }
    }
}