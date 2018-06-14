package info.czekanski.bet.domain.home.cells

import info.czekanski.bet.misc.Cell

data class WelcomeCell(
        val nick: String?,
        val showOpenLinkMessage: Boolean = true
) : Cell