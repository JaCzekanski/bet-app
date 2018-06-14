package info.czekanski.bet.domain.match

import android.net.Uri
import com.google.firebase.dynamiclinks.*
import info.czekanski.bet.model.Match
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.repository.Friend

data class BetViewState(
        val step: Step = Step.BID,
        val bid: Int = 0,
        val score: Pair<Int, Int> = Pair(0, 0),
        val bet: FirebaseBet? = null,
        val match: Match? = null,
        val showLoader: Boolean = false,
        val nicknames: Map<String, String> = mapOf(),
        val closeView: Boolean = false,
        val friends: List<Friend> = listOf(),
        val shareLink: Uri? = null
) {

    enum class Step {
        BID, SCORE, LIST, FRIENDS
    }

    fun scoreAsString() = "${score.first}:${score.second}"

    fun updateScore(first: Int = score.first, second: Int = score.second) = copy(score = Pair(first, second))

    fun updateNickname(userId: String, nickname: String?): BetViewState {
        val mutable = nicknames.toMutableMap()
        mutable[userId] = nickname ?: "? ? ?"
        return copy(nicknames = mutable)
    }
}
