package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.R
import info.czekanski.bet.domain.match.hide
import info.czekanski.bet.domain.match.show
import info.czekanski.bet.domain.match.summary.cells.EntryCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_entry.*


class EntryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: EntryCell) {
        with(cell) {
            textNick.text = nick
            textScore.text = "%d - %d".format(score.first, score.second)

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