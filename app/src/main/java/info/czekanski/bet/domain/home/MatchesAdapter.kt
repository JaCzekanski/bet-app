package info.czekanski.bet.domain.home

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.Cell
import info.czekanski.bet.domain.home.cells.HeaderCell
import info.czekanski.bet.domain.home.cells.WelcomeCell
import info.czekanski.bet.domain.home.cells.MatchCell
import info.czekanski.bet.domain.home.view_holder.WelcomeViewHolder
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder
import info.czekanski.bet.domain.home.view_holder.HeaderViewHolder

class MatchesAdapter(
        private val cells: List<Cell>,
        private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_WELCOME -> WelcomeViewHolder(parent.inflate(R.layout.holder_welcome))
        TYPE_HEADER -> HeaderViewHolder(parent.inflate(R.layout.holder_header))
        TYPE_MATCH -> MatchViewHolder(parent.inflate(R.layout.holder_match), callback)
        else -> throw RuntimeException("Unknown viewType $viewType for MatchesAdapter")
    }

    override fun getItemCount() = cells.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is WelcomeViewHolder -> holder.bind(cells[position] as WelcomeCell)
        is HeaderViewHolder -> holder.bind(cells[position] as HeaderCell)
        is MatchViewHolder -> holder.bind(cells[position] as MatchCell)
        else -> throw RuntimeException("Unknown viewholder for position $position")
    }

    override fun getItemId(position: Int): Long {
        return cells[position].hashCode().toLong()
    }

    override fun getItemViewType(position: Int) = when(cells[position]) {
        is WelcomeCell -> TYPE_WELCOME
        is HeaderCell -> TYPE_HEADER
        is MatchCell -> TYPE_MATCH
        else -> throw RuntimeException("Unknown viewtype for position $position")
    }

    companion object {
        const val TYPE_WELCOME = 1
        const val TYPE_HEADER = 2
        const val TYPE_MATCH = 3
    }

    private fun ViewGroup.inflate(@LayoutRes layout: Int): View =
            LayoutInflater.from(context).inflate(layout, this, false)
}

typealias Callback = (Cell) -> Unit