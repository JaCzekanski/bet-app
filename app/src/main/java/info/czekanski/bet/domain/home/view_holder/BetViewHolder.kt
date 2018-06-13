package info.czekanski.bet.domain.home.view_holder

import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.request.RequestOptions
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.Callback
import info.czekanski.bet.domain.home.cells.BetCell
import info.czekanski.bet.domain.match.hide
import info.czekanski.bet.domain.match.show
import info.czekanski.bet.misc.GlideApp
import info.czekanski.bet.network.firebase.model.FirebaseBetEntry
import info.czekanski.bet.user.UserProvider
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_bet.*
import kotlinx.android.synthetic.main.layout_match.*
import java.text.SimpleDateFormat
import java.util.*


class BetViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val userProvider by lazy { UserProvider.instance }

    fun bind(cell: BetCell) {
        val match = cell.bet.match
        if (match != null) {
            date.text = formatter.format(match.date)
            team1.text = getCountryName(match.team1)
            team2.text = getCountryName(match.team2)

            GlideApp.with(flag1.context)
                    .load(Uri.parse("file:///android_asset/flags/${match.team1}.png"))
                    .centerInside()
                    .into(flag1)

            GlideApp.with(flag2.context)
                    .load(Uri.parse("file:///android_asset/flags/${match.team2}.png"))
                    .centerInside()
                    .apply(RequestOptions.circleCropTransform())
                    .into(flag2)

            score.text = match.score ?: "0 - 0"
            score.setTextColor(ContextCompat.getColor(containerView.context, if (match.score != null) R.color.textActive else R.color.textInactive))
        }

        myScore.show()

        containerView.setOnClickListener {
            callback(cell)
        }

        val userBet = cell.bet.bets[userProvider.userId]
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

            myScore.text = userBet?.score ?: "? - ?"

            textPeopleCount.text = formatPeopleCount(cell.bet.users.size)
            textJackpot.text = formatJackpot(cell.bet.bets.values)

            buttonMore.setOnClickListener {
                callback(cell)
            }
        }

        button.hide()
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

    companion object {
        val formatter = SimpleDateFormat("H:mm  .  d.MM", Locale.US)

        fun getCountryName(code: String): String {
            return Locale("", code.toUpperCase()).displayCountry
        }
    }
}