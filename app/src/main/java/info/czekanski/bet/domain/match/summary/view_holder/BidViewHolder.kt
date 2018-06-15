package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.match.summary.Callback
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_bid.*


class BidViewHolder(override val containerView: View, private val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: BidCell) {
        button.setOnClickListener {
            callback(cell)
        }
    }
}