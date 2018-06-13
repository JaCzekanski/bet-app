package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v7.widget.*
import android.view.*
import info.czekanski.bet.*
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.*
import kotlinx.android.extensions.*
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