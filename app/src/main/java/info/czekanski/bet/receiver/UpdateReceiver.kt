package info.czekanski.bet.receiver

import android.content.*
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import info.czekanski.bet.repository.PreferencesProvider
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val preferences = PreferencesProvider.getInstance(context)
        val userProvider = UserProvider.instance

        if (!preferences.deviceRegistered) {
            val token = FirebaseInstanceId.getInstance().token
            if (token == null) {
                Timber.d("Token == null, exiting")
                return
            }
            Timber.d("Sending token")
            userProvider.setFcmToken(token)
                    .subscribeBy(
                            onComplete = { Timber.d("Success") },
                            onError = { Timber.e(it,"Failure") }
                    )
        }
    }
}
