package info.czekanski.bet.domain.bets


import android.arch.lifecycle.ViewModelProviders
import info.czekanski.bet.domain.base.BaseHomeFragment
import info.czekanski.bet.misc.safeObserve

class BetsFragment : BaseHomeFragment() {
    override fun initializeViewModel() {
        val viewModel = ViewModelProviders.of(this).get(BetsViewModel::class.java)
        viewModel.getCells().safeObserve(this, { matchesAdapter.setCells(it) })
    }
}