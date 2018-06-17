package info.czekanski.bet.domain.home.cells

import info.czekanski.bet.misc.Cell

data class ResultsCell(
        val bettedMatches: Int,
        val wonMatches: Int
) : Cell