package info.czekanski.bet.domain.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import durdinapps.rxfirebase2.RxFirestore
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.utils.ListDecorator
import info.czekanski.bet.domain.match.MatchFragment
import info.czekanski.bet.domain.match.withArgument
import info.czekanski.bet.misc.Cell
import info.czekanski.bet.misc.applySchedulers
import info.czekanski.bet.misc.subscribeBy
import info.czekanski.bet.model.Match
import info.czekanski.bet.network.firebase.model.FirebaseBet
import info.czekanski.bet.user.UserProvider
import io.reactivex.rxkotlin.Flowables
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    val firestore by lazy { FirebaseFirestore.getInstance() }
    val userProvider by lazy { UserProvider.instance }
    private var matchesAdapter = MatchesAdapter(callback = {
        when (it) {
            is MatchCell -> goToMatchView(it.match)
        }
    })


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = matchesAdapter
        recyclerView.addItemDecoration(ListDecorator())
        recyclerView.setHasFixedSize(true)
        (recyclerView.itemAnimator as SimpleItemAnimator).setChangeDuration(0)


        loadMatchesAndBets()
    }

    private fun loadMatchesAndBets() {
        val betsFlowable = RxFirestore.observeQueryRef(firestore.collection("bets").whereEqualTo("users.8764327423567", true))
                .map {
                    it.documents
                            .filterNotNull()
                            .map { it.toObject(FirebaseBet::class.java)!!.copy(id = it.id) }
                }
                .applySchedulers()

        val matchesFlowable = RxFirestore.observeQueryRef(firestore.collection("matches"))
                .map {
                    it.documents
                            .filterNotNull()
                            .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                }
                .applySchedulers()


        val userNameFlowable = userProvider.loadNick().toFlowable()

        Flowables.combineLatest(betsFlowable, matchesFlowable, userNameFlowable, { b, m, _ -> Pair(b, m) })
                .doOnSubscribe {
                    matchesAdapter.setCells(listOf(LoaderCell()))
                }
                .autoDisposable(AndroidLifecycleScopeProvider.from(this))
                .subscribeBy(onNext = {
                    val (bets, matches) = it
                    val cells = mutableListOf<Cell>(WelcomeCell(userProvider.nick))

                    if (bets.isNotEmpty()) {
                        val bets = bets.map { bet ->
                            bet.copy(match = matches.find { match ->
                                match.id == bet.matchId
                            })
                        }
                        cells += HeaderCell("Twoje typy")
                        cells += bets.map { BetCell(it) }
                    }

                    if (matches.isNotEmpty()) {
                        cells += HeaderCell("Najbliższe mecze")
                        cells += matches.map { MatchCell(it) }
                    }

                    matchesAdapter.setCells(cells)
                }, onError = {
                    Log.e("HomeFragment", "LoadMatchesAndBets", it)
                })
    }

    private fun goToMatchView(match: Match) {
//        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
        requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, MatchFragment().withArgument(match))
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}