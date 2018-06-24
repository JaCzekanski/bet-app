package info.czekanski.bet.domain.home

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.*
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.view_holder.*
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.user.UserProvider
import javax.inject.Inject

class MatchesAdapter @Inject constructor(
        val userProvider: UserProvider
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var callback: Callback = {}
    private var cells: List<Cell> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_WELCOME -> WelcomeViewHolder(parent.inflate(R.layout.holder_home_welcome))
        TYPE_HEADER -> HeaderViewHolder(parent.inflate(R.layout.holder_home_header))
        TYPE_MATCH -> MatchViewHolder(parent.inflate(R.layout.holder_home_match), callback)
        TYPE_BET -> BetViewHolder(parent.inflate(R.layout.holder_home_bet), callback, userProvider)
        TYPE_LOADER -> StaticViewHolder(parent.inflate(R.layout.holder_home_loader))
        TYPE_RESULTS -> ResultsViewHolder(parent.inflate(R.layout.holder_home_results))
        TYPE_EMPTY -> EmptyViewHolder(parent.inflate(R.layout.holder_home_empty))
        else -> throw RuntimeException("Unknown viewType $viewType for MatchesAdapter")
    }

    override fun getItemCount() = cells.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is WelcomeViewHolder -> holder.bind(cells[position] as WelcomeCell)
        is HeaderViewHolder -> holder.bind(cells[position] as HeaderCell)
        is MatchViewHolder -> holder.bind(cells[position] as MatchCell)
        is BetViewHolder -> holder.bind(cells[position] as BetCell)
        is ResultsViewHolder -> holder.bind(cells[position] as ResultsCell)
        is EmptyViewHolder -> holder.bind(cells[position] as EmptyCell)
        else -> {
            //throw RuntimeException("Unknown viewholder for position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        val cell = cells[position]
        return when (cell) {
            is WelcomeCell -> -1000
            is HeaderCell -> cell.name.hashCode().toLong()
            is LoaderCell -> -1002
            is MatchCell -> cell.match.id.hashCode().toLong()
            is BetCell -> cell.bet.id.hashCode().toLong()
            is ResultsCell -> -1003
            is EmptyCell -> -1004
            else -> 0
        }
    }

    override fun getItemViewType(position: Int) = when (cells[position]) {
        is WelcomeCell -> TYPE_WELCOME
        is HeaderCell -> TYPE_HEADER
        is MatchCell -> TYPE_MATCH
        is BetCell -> TYPE_BET
        is LoaderCell -> TYPE_LOADER
        is ResultsCell -> TYPE_RESULTS
        is EmptyCell -> TYPE_EMPTY
        else -> throw RuntimeException("Unknown viewtype for position $position")
    }

    fun setCells(new: List<Cell>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return cells[oldItemPosition].hashCode() == new[newItemPosition].hashCode()
            }

            override fun getOldListSize() = cells.size

            override fun getNewListSize() = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return cells[oldItemPosition] == new[newItemPosition]
            }
        }).dispatchUpdatesTo(this)
        this.cells = new
    }

    companion object {
        const val TYPE_WELCOME = 1
        const val TYPE_HEADER = 2
        const val TYPE_MATCH = 3
        const val TYPE_BET = 4
        const val TYPE_LOADER = 5
        const val TYPE_RESULTS = 6
        const val TYPE_EMPTY = 7
    }

    private fun ViewGroup.inflate(@LayoutRes layout: Int): View =
            LayoutInflater.from(context).inflate(layout, this, false)
}

typealias Callback = (Cell) -> Unit