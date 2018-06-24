package info.czekanski.bet.receiver

import android.content.*
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.*
import info.czekanski.bet.repository.PreferencesProvider
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class UpdateReceiver : DaggerBroadcastReceiver() {
    @Inject lateinit var userProvider: UserProvider
    @Inject lateinit var preferences: PreferencesProvider

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

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
                            onError = { Timber.e(it, "Failure") }
                    )
        }
    }
}
