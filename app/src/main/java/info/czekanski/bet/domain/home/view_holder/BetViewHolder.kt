package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.MyApplication
import info.czekanski.bet.domain.home.Callback
import info.czekanski.bet.domain.home.cells.BetCell
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.MatchState
import info.czekanski.bet.network.firebase.model.FirebaseBetEntry
import info.czekanski.bet.user.UserProvider
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_bet.*
import javax.inject.Inject


class BetViewHolder(
        override val containerView: View,
        val callback: Callback,
        val userProvider: UserProvider
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: BetCell) {
        val userBet = cell.bet.bets[userProvider.userId]

        val isAfterMatch = cell.bet.match?.state == MatchState.AFTER

        val smallText = when {
            isAfterMatch && userBet?.score == cell.bet.match?.score -> "TRAFIONY"
            isAfterMatch && userBet?.score != cell.bet.match?.score -> "PUDŁO"
            !isAfterMatch && cell.bet.match?.state == MatchState.DURING -> "TRWA"
            else -> ""
        }

        cell.bet.match?.let { viewMatch.bindMatch(it, userScore = smallText) }

        containerView.setOnClickListener { callback(cell) }

        if (cell.bet.bets.size == 1 && userBet != null) {
            viewSeparator.hide()
            layoutBottom.hide()

            if (isAfterMatch) {
                buttonInvite.hide()
            } else {
                buttonInvite.show()
                buttonInvite.setOnClickListener {
                    callback(cell) // TODO: Change me
                }
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