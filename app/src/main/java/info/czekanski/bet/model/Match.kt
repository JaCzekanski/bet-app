package info.czekanski.bet.model

import java.util.*

data class Match(
        val team1: Team = "",
        val team2: Team = "",
        val date: Date = Date()
)
typealias Team = String