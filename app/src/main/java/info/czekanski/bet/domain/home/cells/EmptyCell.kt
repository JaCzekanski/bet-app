package info.czekanski.bet.domain.home.cells

import android.support.annotation.StringRes
import info.czekanski.bet.R
import info.czekanski.bet.misc.Cell

data class EmptyCell(
        @StringRes val text: Int
) : Cell {
    companion object {
        private val responses = listOf(
                R.string.empty_text_1,
                R.string.empty_text_2,
                R.string.empty_text_3
        )

        fun create() = EmptyCell(responses.shuffled().first())
    }
}
