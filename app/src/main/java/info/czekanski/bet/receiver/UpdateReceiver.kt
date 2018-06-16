package info.czekanski.bet.receiver

import android.content.*
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import info.czekanski.bet.repository.PreferencesProvider
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val preferences = PreferencesProvider.getInstance(context)
        val userProvider = UserProvider.instance

        if (!preferences.deviceRegistered) {
            val token = FirebaseInstanceId.getInstance().token
            if (token == null) {
                Log.d("UpdateReceiver", "Token == null, exiting")
                return
            }
            Log.d("UpdateReceiver", "Sending token")
            userProvider.setFcmToken(token)
                    .subscribeBy(
                            onComplete = { Log.d("MainActivity", "Success") },
                            onError = { Log.e("MainActivity", "Failure", it) }
                    )
        }
    }
}
