package info.czekanski.bet.domain.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.cells.HeaderCell
import info.czekanski.bet.domain.home.cells.MatchCell
import info.czekanski.bet.domain.home.cells.WelcomeCell
import info.czekanski.bet.domain.home.utils.ListDecorator
import info.czekanski.bet.domain.match.bundleOf
import info.czekanski.bet.model.Match
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = MatchesAdapter(listOf(), {})
        recyclerView.addItemDecoration(ListDecorator())

        FirebaseFirestore.getInstance()
                .collection("matches")
                .addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot?.documents == null) return@addSnapshotListener
                    val matches: List<MatchCell> = querySnapshot.documents.filterNotNull().map { it.toObject(Match::class.java)!! }.map { MatchCell(it) }
                    recyclerView.adapter = MatchesAdapter(listOf(
                            WelcomeCell("Krachtan"),
                            HeaderCell("Najbliższe mecze")
                    ) + matches, {
                        if (it is MatchCell) {
                            goToMatchView(it.match)
                        }
                    })
                }
    }

    private fun goToMatchView(match: Match) {
        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
    }
}