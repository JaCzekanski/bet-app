package info.czekanski.bet.service

import android.util.Log
import com.google.firebase.iid.*
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy

class FirebaseTokenRegistrationService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d("FirebaseToken", "onTokenRefresh: $token")

        val userProvider = UserProvider.instance
        if (userProvider.loggedIn && token != null) {
            userProvider.setFcmToken(token)
                    .subscribeBy(onComplete = {
                        Log.d("FirebaseToken", "Device registered")
                    }, onError = {
                        Log.e("FirebaseToken", "Unable to register device", it)
                    })
        }
    }
}