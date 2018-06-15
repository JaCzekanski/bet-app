package info.czekanski.bet.repository

import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.network.firebase.model.FirebaseBet
import io.reactivex.*

class FriendsRepository(private val firestore: FirebaseFirestore) {
    private val cache = mutableMapOf<String, String?>()

    fun getFriends(userId: String): Single<List<Friend>> {
        return RxFirestore.getCollection(firestore.collection("bets").whereEqualTo("users.$userId", true))
                .map {
                    it.documents
                            .filterNotNull()
                            .map { it.toObject(FirebaseBet::class.java)!!.copy(id = it.id) }
                            .flatMap { it.bets.keys }
                            .distinct()
                }
                .flattenAsFlowable { it }
                .filter { id -> id != userId } // Filter out myself
                .flatMap { friendId ->
                    getName(friendId)
                            .map { userName -> Friend(friendId, userName) }
                            .toFlowable()
                }
                .toSortedList()
                .applySchedulers()
    }

    fun getName(userId: String): Maybe<String> {
        if (cache.contains(userId)) return Maybe.just(cache[userId])

        return RxFirestore.getDocument(firestore.collection("users").document(userId))
                .map { it.getString("nick") ?: "" }
                .doOnSuccess { nick -> cache[userId] = nick }
                .applySchedulers()
    }

    companion object {
        val instance by lazy {
            FriendsRepository(FirebaseFirestore.getInstance())
        }
    }
}

data class Friend(
        val id: String,
        val name: String?
) : Comparable<Friend> {
    override fun compareTo(other: Friend): Int {
        if (name == null || other.name == null) return 0
        return name.compareTo(other.name)
    }

    companion object {
        val SHARE = Friend("", "Udostępnij")
    }
}