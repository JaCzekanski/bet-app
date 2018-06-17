package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.cells.EmptyCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_empty.*


class EmptyViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(cell: EmptyCell) {
        textEmpty.text = containerView.resources.getString(cell.text)
    }
}