package info.czekanski.bet.user

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirestore
import info.czekanski.bet.misc.applySchedulers
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class UserProvider private constructor(val firestore: FirebaseFirestore, val auth: FirebaseAuth) {
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

    fun setNick(nick: String): Completable {
        val userId = userId ?: return Completable.error(RuntimeException("Not logged in"))

        return RxFirestore.setDocument(firestore.collection("users").document(userId), mapOf("nick" to nick))
                .applySchedulers()
                .doOnEvent { this.nick = nick }
    }

    fun loadNick(): Single<String> {
        if (!loggedIn) return Single.just("")
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


    companion object {
        val instance by lazy {
            UserProvider(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
        }
    }
}