package info.czekanski.bet.domain.calendar


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.*
import android.support.v4.app.*
import android.support.v7.widget.*
import android.util.Log
import android.view.*
import com.google.firebase.firestore.*
import com.uber.autodispose.android.lifecycle.*
import com.uber.autodispose.kotlin.*
import durdinapps.rxfirebase2.*
import info.czekanski.bet.R
import info.czekanski.bet.R.id.recyclerView
import info.czekanski.bet.domain.home.*
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.utils.*
import info.czekanski.bet.domain.match.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.network.firebase.model.*
import info.czekanski.bet.repository.*
import info.czekanski.bet.user.*
import io.reactivex.rxkotlin.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.Pair
import kotlin.getValue
import kotlin.lazy

class CalendarFragment : Fragment() {
    private var matchesAdapter = MatchesAdapter(callback = this::onCellClicked)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        val viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        viewModel.getCells().safeObserve(this, { matchesAdapter.setCells(it) })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = matchesAdapter
        recyclerView.addItemDecoration(ListDecorator())
        recyclerView.setHasFixedSize(true)
        (recyclerView.itemAnimator as SimpleItemAnimator).setChangeDuration(0)
    }

    private fun onCellClicked(cell: Cell) {
        when (cell) {
            is MatchCell -> goToMatchView(BetFragment.Argument(matchId = cell.match.id))
        }
    }

    private fun goToMatchView(arg: BetFragment.Argument) {
//        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
        requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, BetFragment().withArgument(arg))
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}