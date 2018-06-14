package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.R
import info.czekanski.bet.domain.match.summary.cells.EntryCell
import info.czekanski.bet.misc.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_entry.*


class EntryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

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
                textWon.text = "+ $won zł"
                textWon.show()
            } else {
                layout.setBackgroundResource(0)
                textWon.hide()
            }
        }
    }
}