package info.czekanski.bet.model

import info.czekanski.bet.model.MatchState.BEFORE
import java.util.*

data class Match(
        val id: String = "",
        val team1: Team = "",
        val team2: Team = "",
        val date: Date = Date(),
        val event: Int = 0,
        val score: String? = null,
        val state: String = BEFORE
)
typealias Team = String