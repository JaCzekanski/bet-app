package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.*
import android.view.*
import info.czekanski.bet.domain.home.*
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.network.firebase.model.*
import info.czekanski.bet.user.*
import kotlinx.android.extensions.*
import kotlinx.android.synthetic.main.holder_home_bet.*
import java.text.*
import java.util.*


class BetViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val userProvider by lazy { UserProvider.instance }

    fun bind(cell: BetCell) {
        val userBet = cell.bet.bets[userProvider.userId]

        cell.bet.match?.let { viewMatch.bindMatch(it, userScore = userBet?.score ?: "? - ?") }

        containerView.setOnClickListener { callback(cell) }

        if (cell.bet.bets.size == 1 && userBet != null) {
            viewSeparator.hide()
            layoutBottom.hide()
            buttonInvite.show()
            buttonInvite.setOnClickListener {
                callback(cell) // TODO: Change me
            }
        } else {
            viewSeparator.show()
            layoutBottom.show()
            buttonInvite.hide()

            textPeopleCount.text = formatPeopleCount(cell.bet.users.size)
            textJackpot.text = formatJackpot(cell.bet.bets.values)

            buttonMore.setOnClickListener {
                callback(cell)
            }
        }
    }

    private fun formatJackpot(bets: Collection<FirebaseBetEntry>): String {
        val jackpot = bets.mapNotNull { it.bid }
                .reduce { acc, i -> acc + i }
        return "$jackpot zł"
    }

    private fun formatPeopleCount(count: Int): String {
        return when (count) {
            1 -> "$count osoba"
            2, 3, 4 -> "$count osoby"
            else -> "$count osób"
        }
    }
}