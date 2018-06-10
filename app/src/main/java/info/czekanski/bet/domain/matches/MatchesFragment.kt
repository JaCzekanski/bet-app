package info.czekanski.bet.domain.matches


import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import info.czekanski.bet.R
import info.czekanski.bet.domain.matches.cells.HeaderCell
import info.czekanski.bet.domain.matches.cells.WelcomeCell
import info.czekanski.bet.domain.matches.cells.MatchCell
import info.czekanski.bet.model.Match
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseFirestore.getInstance()
                .collection("matches")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot?.documents == null) return@addSnapshotListener
                    val matches: List<MatchCell> = querySnapshot.documents.filterNotNull().map { it.toObject(Match::class.java)!! }.map { MatchCell(it) }
                    recyclerView.adapter = MatchesAdapter(listOf(
                            WelcomeCell("Krachtan"),
                            HeaderCell("Najbliższe mecze")
                    ) + matches)

                    recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                            outRect.top = 8.dpToPx
                            outRect.bottom = 8.dpToPx
                        }
                    })
                }
    }

    val Int.dpToPx: Int
        get() = (this * resources.displayMetrics.density).toInt()
}