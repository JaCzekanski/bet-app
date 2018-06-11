package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.cells.HeaderCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_header.*


class HeaderViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: HeaderCell) {
        title.text = cell.name
    }
}