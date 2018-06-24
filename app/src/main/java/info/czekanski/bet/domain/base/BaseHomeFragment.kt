package info.czekanski.bet.domain.base


import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.*
import info.czekanski.bet.R
import info.czekanski.bet.di.utils.BaseFragment
import info.czekanski.bet.domain.game.GameFragment
import info.czekanski.bet.domain.home.MatchesAdapter
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.utils.ListDecorator
import info.czekanski.bet.misc.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

abstract class BaseHomeFragment : BaseFragment() {
    @Inject lateinit var matchesAdapter: MatchesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initializeViewModel()
    }

    protected abstract fun initializeViewModel()

    private fun initRecyclerView() {
        matchesAdapter.callback = this::onCellClicked

        recyclerView.adapter = matchesAdapter
        recyclerView.addItemDecoration(ListDecorator())
        recyclerView.setHasFixedSize(true)
        (recyclerView.itemAnimator as SimpleItemAnimator).changeDuration = 0
    }

    private fun onCellClicked(cell: Cell) {
        when (cell) {
            is MatchCell -> goToMatchView(GameFragment.Argument(matchId = cell.match.id))
            is BetCell -> goToMatchView(GameFragment.Argument(betId = cell.bet.id))
        }
    }

    private fun goToMatchView(arg: GameFragment.Argument) {
        requireActivity().navigateWithSlide(GameFragment().withArgument(arg), true)
    }
}