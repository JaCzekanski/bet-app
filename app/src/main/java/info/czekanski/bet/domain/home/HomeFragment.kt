package info.czekanski.bet.domain.home


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SimpleItemAnimator
import android.view.*
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.utils.ListDecorator
import info.czekanski.bet.domain.match.BetFragment
import info.czekanski.bet.misc.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var matchesAdapter = MatchesAdapter(callback = this::onCellClicked)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.getCells().safeObserve(this, { matchesAdapter.setCells(it) })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = matchesAdapter
        recyclerView.addItemDecoration(ListDecorator())
        recyclerView.setHasFixedSize(true)
        (recyclerView.itemAnimator as SimpleItemAnimator).changeDuration = 0
    }

    private fun onCellClicked(cell: Cell) {
        when (cell) {
            is MatchCell -> goToMatchView(BetFragment.Argument(matchId = cell.match.id))
            is BetCell -> goToMatchView(BetFragment.Argument(betId = cell.bet.id))
        }
    }

    private fun goToMatchView(arg: BetFragment.Argument) {
        requireActivity().navigateWithSlide(BetFragment().withArgument(arg), true)
    }
}