package info.czekanski.bet.domain.matches


import android.arch.lifecycle.ViewModelProviders
import info.czekanski.bet.domain.base.BaseHomeFragment
import info.czekanski.bet.misc.*

class MatchesFragment : BaseHomeFragment() {
    override fun initializeViewModel() {
        val viewModel = ViewModelProviders.of(this).get(MatchesViewModel::class.java)
        viewModel.getCells().safeObserve(this, { matchesAdapter.setCells(it) })
    }
}