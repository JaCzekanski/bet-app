package info.czekanski.bet.service

import android.app.*
import android.content.*
import android.net.Uri
import android.os.Build
import android.support.v4.app.*
import android.util.Log
import com.google.firebase.messaging.*
import info.czekanski.bet.*
import info.czekanski.bet.R
import timber.log.Timber
import java.util.*


class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d( "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            Timber.d( "Message data payload: $data")

            val title = data["title"]
            val body = data["body"]
            val type = data["type"]
            val deeplink = data["deeplink"]

            if (title != null && body != null && type != null) {
                displayNotitication(title, body, type, deeplink)
            }
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun displayNotitication(title: String, body: String, type: String, deeplink: String?) {
        val channelId = when (type) {
            "INVITE" -> CHANNEL_INVITE
            "REMINDER" -> CHANNEL_REMINDER
            "SCORE" -> CHANNEL_SCORE
            else -> CHANNEL_DEFAULT
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (deeplink != null) intent.setData(Uri.parse(deeplink))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val notificationId = Random().nextInt()

        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_ball)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        const val CHANNEL_INVITE = "INVITE"
        const val CHANNEL_REMINDER = "REMINDER"
        const val CHANNEL_SCORE = "SCORE"
        const val CHANNEL_DEFAULT = "DEFAULT"

        fun createNotificationChannels(context: Context) {
            fun createNotificationChannel(id: String, name: String, description: String, importance: Int? = null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

                val channel = NotificationChannel(id, name, importance
                        ?: NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = description

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }

            createNotificationChannel(CHANNEL_INVITE, context.getString(R.string.zaproszenie), context.getString(R.string.zaproszenie_desc))
            createNotificationChannel(CHANNEL_REMINDER, context.getString(R.string.przypomnienie), context.getString(R.string.przypomnienie_desc))
            createNotificationChannel(CHANNEL_SCORE, context.getString(R.string.wynik), context.getString(R.string.wynik_desc))
            createNotificationChannel(CHANNEL_DEFAULT, context.getString(R.string.pozostale), context.getString(R.string.pozostale_desc))
        }

    }
}