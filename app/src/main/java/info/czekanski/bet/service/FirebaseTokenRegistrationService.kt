package info.czekanski.bet.service

import com.google.firebase.iid.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class FirebaseTokenRegistrationService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Timber.d("onTokenRefresh: $token")

        val userProvider = UserProvider.instance
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