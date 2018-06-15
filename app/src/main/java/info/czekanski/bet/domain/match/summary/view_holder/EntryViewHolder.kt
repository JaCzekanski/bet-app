package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import android.support.v7.widget.*
import android.view.View
import com.github.florent37.viewtooltip.ViewTooltip
import info.czekanski.bet.R
import info.czekanski.bet.domain.match.summary.Callback
import info.czekanski.bet.domain.match.summary.cells.EntryCell
import info.czekanski.bet.misc.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_entry.*


class EntryViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: EntryCell) {
        with(cell) {
            textNick.text = nick
            textNick.setTextColor(getColor(containerView.context, if (score == null) R.color.textNickInctive else R.color.textNickActive))

            if (score == null) {
                textScore.hide()
            } else {
                textScore.text = "%d - %d".format(score.first, score.second)
                textScore.show()
            }

            if (won != null) {
                layout.setBackgroundResource(R.color.yellow)
                textWon.text = "+ %d zł".format(won)
                textWon.show()
            } else {
                layout.setBackgroundResource(0)
                textWon.hide()
            }


            containerView.setOnClickListener {
                ViewTooltip
                        .on(textNick)
                        .autoHide(true, 1500)
                        .corner(30)
                        .color(getColor(containerView.context, R.color.darkBackground))
                        .position(ViewTooltip.Position.TOP)
                        .text("Stawka: %d zł".format(cell.bid))
                        .show()
            }
        }
    }
}