package info.czekanski.bet.domain.match.summary.view_holder

import android.support.v7.widget.*
import android.view.*
import info.czekanski.bet.domain.match.summary.*
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.*
import kotlinx.android.extensions.*
import kotlinx.android.synthetic.main.holder_summary_invite.*


class InviteViewHolder(override val containerView: View, private val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: InviteCell) {
        textInviteFriends.show(cell.showText)
        buttonInvite.setOnClickListener {
            callback(cell)
        }
    }
}