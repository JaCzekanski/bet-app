package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.cells.WelcomeCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_welcome.*


class WelcomeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: WelcomeCell) {
        if (cell.nick == null) {
            textNick.text = "Witaj"
        } else {
            textNick.text = "Hej, ${cell.nick}."
        }
    }
}