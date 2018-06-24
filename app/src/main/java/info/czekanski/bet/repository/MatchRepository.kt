package info.czekanski.bet.repository

import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.model.Match
import io.reactivex.Flowable
import javax.inject.Inject

class MatchRepository @Inject constructor(
        private val firestore: FirebaseFirestore
) {

    fun getMatches(): Flowable<List<Match>> {
        return RxFirestore.observeQueryRef(firestore.collection("matches").orderBy("date"))
                .map {
                    it.documents
                            .filterNotNull()
                            .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                }
                .applySchedulers()
    }

    fun observeMatch(matchId: String): Flowable<Match> {
        return RxFirestore.observeDocumentRef(firestore.collection("matches").document(matchId))
                .filter { it.exists() }
                .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                .applySchedulers()
    }
}