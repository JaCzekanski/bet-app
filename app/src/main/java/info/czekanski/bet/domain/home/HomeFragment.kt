package info.czekanski.bet.domain.home


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
import info.czekanski.bet.domain.home.cells.*
import info.czekanski.bet.domain.home.utils.*
import info.czekanski.bet.domain.match.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.*
import info.czekanski.bet.network.firebase.model.*
import info.czekanski.bet.user.*
import io.reactivex.rxkotlin.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.Pair
import kotlin.getValue
import kotlin.lazy

class HomeFragment : Fragment() {
    val firestore by lazy { FirebaseFirestore.getInstance() }
    val userProvider by lazy { UserProvider.instance }
    private var matchesAdapter = MatchesAdapter(callback = {
        when (it) {
            is MatchCell -> goToMatchView(MatchFragment.Argument(matchId = it.match.id))
            is BetCell -> goToMatchView(MatchFragment.Argument(betId = it.bet.id))
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
        val betsFlowable = RxFirestore.observeQueryRef(firestore.collection("bets").whereEqualTo("users.${userProvider.userId!!}", true))
                .map {
                    it.documents
                            .filterNotNull()
                            .map { it.toObject(FirebaseBet::class.java)!!.copy(id = it.id) }
                }
                .applySchedulers()

        val matchesFlowable = RxFirestore.observeQueryRef(firestore.collection("matches").orderBy("date"))
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
                        val betsWithMatches = bets.map { bet ->
                            bet.copy(match = matches.find { match ->
                                match.id == bet.matchId
                            })
                        }
                        cells += HeaderCell("Twoje typy")
                        cells += betsWithMatches.map { BetCell(it) }
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

    private fun goToMatchView(arg: MatchFragment.Argument) {
//        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
        requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, MatchFragment().withArgument(arg))
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}