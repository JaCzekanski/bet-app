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
    val betService: BetService by lazy { BetService.instance }
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
                    val matches: List<MatchCell> = querySnapshot.documents
                            .filterNotNull()
                            .map { it.toObject(Match::class.java)!!.copy(id = it.id) }
                            .map { MatchCell(it) }

                    recyclerView.adapter = MatchesAdapter(listOf(
                            WelcomeCell("Krachtan"),
                            HeaderCell("Najbliższe mecze")
                    ) + matches, {
                        if (it is MatchCell) {
                            if (it.match.id.isEmpty()) {
                                Toast.makeText(context, "Invalid match id!", Toast.LENGTH_SHORT).show()
                                return@MatchesAdapter
                            }

                            // Create bet and go to it
                            betService.api.createBet(it.match.id, "XDXDXDXDXD")
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeBy(onSuccess = { result ->
                                        result.id // TODO: USE ME!!!
                                        goToMatchView(it.match)
                                    }, onError = {
                                        Toast.makeText(context, "Unable to create bet!", Toast.LENGTH_SHORT).show()
                                        Log.w("CreateBet", it)
                                    })
                        }
                    })
                }
    }

    private fun goToMatchView(match: Match) {
        findNavController().navigate(R.id.actionOpenMatch, bundleOf(match))
    }
}