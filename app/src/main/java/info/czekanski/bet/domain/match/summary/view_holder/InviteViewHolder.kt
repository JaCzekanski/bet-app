package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.match.show
import info.czekanski.bet.domain.match.summary.Callback
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.InviteCell
import info.czekanski.bet.domain.match.summary.cells.SummaryCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_summary_invite.*
import kotlinx.android.synthetic.main.holder_summary_summary.*


class InviteViewHolder(override val containerView: View, private val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: InviteCell) {
        textInviteFriends.show(cell.showText)
        buttonInvite.setOnClickListener {
            callback(cell)
        }
    }
}