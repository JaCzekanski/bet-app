package info.czekanski.bet.domain.matches.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.matches.cells.HeaderCell
import info.czekanski.bet.domain.matches.cells.WelcomeCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_header.*
import kotlinx.android.synthetic.main.holder_welcome.*


class HeaderViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: HeaderCell) {
        title.text = cell.name
    }
}