package info.czekanski.bet.domain.matches


import info.czekanski.bet.domain.base.BaseHomeFragment
import info.czekanski.bet.misc.safeObserve

class MatchesFragment : BaseHomeFragment() {
    override fun initializeViewModel() {
        viewModel<MatchesViewModel>()
                .getCells()
                .safeObserve(this, {
                    matchesAdapter.setCells(it)
                })
    }
}