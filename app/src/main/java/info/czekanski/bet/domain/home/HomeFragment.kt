package info.czekanski.bet.domain.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.HeaderCell
import info.czekanski.bet.domain.home.cells.MatchCell
import info.czekanski.bet.domain.home.cells.WelcomeCell
import info.czekanski.bet.domain.home.utils.ListDecorator
import info.czekanski.bet.domain.match.bundleOf
import info.czekanski.bet.model.Match
import info.czekanski.bet.network.BetService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    val firestore by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = MatchesAdapter(listOf(), {})
        recyclerView.addItemDecoration(ListDecorator())

        loadMatches()
        loadBets()
    }

    private fun loadMatches() {
        firestore.collection("matches")
                .addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot?.documents == null) return@addSnapshotListener
                    val matches: List<MatchCell> = querySnapshot.documents
                            .filterNotNull()
                            .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                            .map { MatchCell(it) }

                    val cells = listOf(
                            WelcomeCell("Krachtan"),
                            HeaderCell("Najbliższe mecze")
                    ) + matches

                    recyclerView.adapter = MatchesAdapter(cells, callback = {
                        when (it) {
                            is MatchCell -> goToMatchView(it.match)
                        }
                    })
                }
    }

    private fun loadBets() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun goToMatchView(match: Match) {
        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
    }
}