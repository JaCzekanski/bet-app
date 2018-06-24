package info.czekanski.bet.domain.home


import info.czekanski.bet.domain.base.BaseHomeFragment
import info.czekanski.bet.misc.safeObserve

class HomeFragment : BaseHomeFragment() {
    override fun initializeViewModel() {
        viewModel<HomeViewModel>()
                .getCells()
                .safeObserve(this, {
                    matchesAdapter.setCells(it)
                })
    }
}