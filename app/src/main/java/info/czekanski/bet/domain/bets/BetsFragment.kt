package info.czekanski.bet.domain.bets


import info.czekanski.bet.domain.base.BaseHomeFragment
import info.czekanski.bet.misc.safeObserve

class BetsFragment : BaseHomeFragment() {
    override fun initializeViewModel() {
        viewModel<BetsViewModel>()
                .getCells()
                .safeObserve(this, {
                    matchesAdapter.setCells(it)
                })
    }
}