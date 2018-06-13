package info.czekanski.bet.domain.home.cells

import info.czekanski.bet.misc.Cell
import info.czekanski.bet.model.Match
import info.czekanski.bet.network.firebase.model.FirebaseBet

data class BetCell(
        val bet: FirebaseBet
) : Cell