package info.czekanski.bet.domain.match

import info.czekanski.bet.model.Match
import info.czekanski.bet.network.firebase.model.FirebaseBet

data class BetViewState(
        val step: Step = Step.BID,
        val bid: Int = 0,
        val score: Pair<Int, Int> = Pair(0, 0),
        val bet: FirebaseBet? = null,
        val match: Match? = null,
        val showLoader: Boolean = false,
        val nicknames: Map<String, String> = mapOf()
) {

    enum class Step {
        BID, SCORE, LIST
    }

    fun scoreAsString() = "${score.first}:${score.second}"

    fun updateScore(first: Int = score.first, second: Int = score.second) = copy(score = Pair(first,second))

    fun updateNickname(userId: String, nickname: String?): BetViewState {
        val mutable = nicknames.toMutableMap()
        mutable[userId] = nickname ?: "? ? ?"
        return copy(nicknames = mutable)
    }
}
