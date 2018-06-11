package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.cells.WelcomeCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_welcome.*


class WelcomeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: WelcomeCell) {
        textNick.text = "Hej, ${cell.nick}."
    }
}