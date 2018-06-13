package info.czekanski.bet.model

import java.util.*

data class Match(
        val id: String = "",
        val team1: Team = "",
        val team2: Team = "",
        val date: Date = Date(),
        val event: Int = 0,
        val score: String? = null,
        val state: String = StateBefore
) {
    companion object {
        const val StateBefore = "BEFORE"
        const val StateDuring = "DURING"
        const val StateAfter = "AFTER"
    }
}
typealias Team = String