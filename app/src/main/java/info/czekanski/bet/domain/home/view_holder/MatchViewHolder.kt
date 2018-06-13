package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.Callback
import info.czekanski.bet.domain.home.cells.MatchCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_match.*


class MatchViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: MatchCell) {
        viewMatch.bindMatch(cell.match, { callback(cell) })
    }
}