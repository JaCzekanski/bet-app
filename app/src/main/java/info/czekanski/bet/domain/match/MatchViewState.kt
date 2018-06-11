package info.czekanski.bet.domain.match

data class MatchViewState(
        var step: Step = Step.BID,
        var bid: Int = 0,
        var score: Pair<Int, Int> = Pair(0, 0)
) {
    enum class Step {
        BID, SCORE, LIST
    }

    fun updateScore(first: Int = score.first, second: Int = score.second) = copy(score = Pair(first,second))
}
