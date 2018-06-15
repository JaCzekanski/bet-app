package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.WelcomeCell
import info.czekanski.bet.misc.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_welcome.*


class WelcomeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: WelcomeCell) {
        if (cell.nick == null) {
            textNick.text = containerView.context.getString(R.string.witaj)
        } else {
            textNick.text = containerView.context.getString(R.string.hej_nick, cell.nick)
        }


        textOpenLink.show(cell.showOpenLinkMessage)
    }
}