package info.czekanski.bet.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.*
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.model.TokenRequest
import info.czekanski.bet.network.BetService
import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy

class UserProvider private constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {
    var nick: String? = null
        private set

    val loggedIn: Boolean
        get() = auth.currentUser != null

    val userId: String?
        get() = auth.currentUser?.uid

    init {
        loadNick()
    }

    fun login(): Completable {
        return RxFirebaseAuth.signInAnonymously(auth)
                .applySchedulers()
                .flatMapCompletable { Completable.complete() }
    }

    fun logout(): Completable {
        val user = auth.currentUser
                ?: return Completable.error(RuntimeException("User not logged in"))

        return Completable
                .create({ emitter -> RxHandlerCompletable.assignOnTask(emitter, user.delete()) })
                .applySchedulers()
    }

    fun setNick(nick: String?): Completable {
        val userId = userId ?: return Completable.error(RuntimeException("Not logged in"))

        val document = firestore.collection("users").document(userId)
        if (nick == null) {
            return RxFirestore.deleteDocument(document)
                    .applySchedulers()
                    .doOnEvent { this.nick = nick }
        }

        return RxFirestore.setDocument(document, mapOf("nick" to nick))
                .applySchedulers()
                .doOnEvent { this.nick = nick }
    }

    fun loadNick(): Single<String> {
        if (!loggedIn) return Single.just("")
        if (nick != null) return Single.just(nick)

        val single = RxFirestore.getDocument(firestore.document("users/$userId"))
                .applySchedulers()
                .map { it.getString("nick") }
                .doOnSuccess { nick = it }
                .flatMapSingle { Single.just(it) }
                .cache()

        single.subscribeBy(onError = {
            Log.e("UserProvider", "loadNick", it)
        })
        return single
    }

    fun setFcmToken(fcmToken: String): Completable {
        return BetService.instance.api.registerDevice(TokenRequest(fcmToken), userId!!)
                .applySchedulers()
    }


    fun removeFcmToken(): Completable {
        return BetService.instance.api.unregisterDevice(userId!!)
                .applySchedulers()
    }


    companion object {
        val instance by lazy {
            UserProvider(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
        }
    }
}