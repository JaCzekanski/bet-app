package info.czekanski.bet.domain.home.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.domain.home.cells.ResultsCell
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_home_results.*


class ResultsViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: ResultsCell) {
        textBetted.text = "${cell.bettedMatches}"
        textWon.text = "${cell.wonMatches}"
    }
}