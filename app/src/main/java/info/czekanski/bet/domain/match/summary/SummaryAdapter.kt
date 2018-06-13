package info.czekanski.bet.domain.match.summary

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import info.czekanski.bet.R
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.domain.match.summary.view_holder.EntryViewHolder
import info.czekanski.bet.domain.match.summary.view_holder.InviteViewHolder
import info.czekanski.bet.domain.match.summary.view_holder.StaticViewHolder
import info.czekanski.bet.domain.match.summary.view_holder.SummaryViewHolder
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.misc.inflate

class SummaryAdapter(
        private val cells: List<Cell>,
        private val callback: Callback = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_HEADER -> StaticViewHolder(parent.inflate(R.layout.holder_summary_header))
        TYPE_SEPARATOR -> StaticViewHolder(parent.inflate(R.layout.holder_summary_separator))
        TYPE_ENTRY -> EntryViewHolder(parent.inflate(R.layout.holder_summary_entry))
        TYPE_SUMMARY -> SummaryViewHolder(parent.inflate(R.layout.holder_summary_summary))
        TYPE_NOTE -> SummaryViewHolder(parent.inflate(R.layout.holder_summary_note))
        TYPE_INVITE -> InviteViewHolder(parent.inflate(R.layout.holder_summary_invite), callback)
        TYPE_LOADER -> StaticViewHolder(parent.inflate(R.layout.holder_summary_loader))
        else -> throw RuntimeException("Unknown viewType $viewType for MatchesAdapter")
    }

    override fun getItemCount() = cells.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is EntryViewHolder -> holder.bind(cells[position] as EntryCell)
        is SummaryViewHolder -> holder.bind(cells[position] as SummaryCell)
        is InviteViewHolder -> holder.bind(cells[position] as InviteCell)
        else -> {
//            throw RuntimeException("Unknown viewholder for position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        return cells[position].hashCode().toLong()
    }

    override fun getItemViewType(position: Int) = when (cells[position]) {
        is HeaderCell -> TYPE_HEADER
        is SeparatorCell -> TYPE_SEPARATOR
        is EntryCell -> TYPE_ENTRY
        is SummaryCell -> TYPE_SUMMARY
        is NoteCell -> TYPE_NOTE
        is InviteCell -> TYPE_INVITE
        is LoaderCell -> TYPE_LOADER
        else -> throw RuntimeException("Unknown viewtype for position $position")
    }

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_SEPARATOR = 2
        const val TYPE_ENTRY = 3
        const val TYPE_SUMMARY = 4
        const val TYPE_NOTE = 5 // nikt nie wygrał
        const val TYPE_INVITE = 6
        const val TYPE_LOADER = 7
    }
}

typealias Callback = (Cell) -> Unit