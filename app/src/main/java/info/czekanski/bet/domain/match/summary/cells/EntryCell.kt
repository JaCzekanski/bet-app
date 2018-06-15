package info.czekanski.bet.domain.match.summary.cells

import info.czekanski.bet.misc.Cell

class EntryCell(
        val nick: String,
        val score: Pair<Int, Int>?,
        val won: Int? = null, // how much have won
        val bid: Int? = null
) : Cell