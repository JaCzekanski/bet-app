package info.czekanski.bet.domain.match

import info.czekanski.bet.network.firebase.model.FirebaseBet

data class MatchViewState(
        val step: Step = Step.BID,
        val bid: Int = 0,
        val score: Pair<Int, Int> = Pair(0, 0),
        val bet: FirebaseBet? = null
) {
    enum class Step {
        BID, SCORE, LIST
    }

    fun scoreAsString() = "${score.first}:${score.second}"

    fun updateScore(first: Int = score.first, second: Int = score.second) = copy(score = Pair(first,second))
}
