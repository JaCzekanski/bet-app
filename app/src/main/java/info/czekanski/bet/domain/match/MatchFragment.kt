package info.czekanski.bet.domain.match

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.RequestOptions
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.utils.ItemDecorator
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder.Companion.getCountryName
import info.czekanski.bet.domain.match.MatchViewState.Step.*
import info.czekanski.bet.domain.match.summary.SummaryAdapter
import info.czekanski.bet.domain.match.summary.cells.*
import info.czekanski.bet.misc.GlideApp
import info.czekanski.bet.model.Match
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.android.synthetic.main.layout_match.*
import kotlinx.android.synthetic.main.layout_match_bid.*
import kotlinx.android.synthetic.main.layout_match_score.*

class MatchFragment : Fragment() {
    val match by lazy { getArgument<Match>() }
    val state: MutableLiveData<MatchViewState> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        bindMatch(match)
        initViews()

        state.observe(this, Observer<MatchViewState> { if (it != null) updateView(it) })

        state.postValue(MatchViewState(step = BID))
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun initViews() {
        buttonMinus.setOnClickListener {
            if (state.v.bid > 0) state.postValue(state.v.copy(bid = state.v.bid - 5))
        }
        buttonPlus.setOnClickListener {
            if (state.v.bid < 100) state.postValue(state.v.copy(bid = state.v.bid + 5))
        }
        buttonAccept.setOnClickListener {
            state.postValue(state.v.copy(step = SCORE))
        }


        buttonMinus1.setOnClickListener {
            if (state.v.score.first > 0) state.postValue(state.v.updateScore(first = state.v.score.first - 1))
        }
        buttonPlus1.setOnClickListener {
            if (state.v.score.first < 9) state.postValue(state.v.updateScore(first = state.v.score.first + 1))
        }

        buttonMinus2.setOnClickListener {
            if (state.v.score.second > 0) state.postValue(state.v.updateScore(second = state.v.score.second - 1))
        }
        buttonPlus2.setOnClickListener {
            if (state.v.score.second < 9) state.postValue(state.v.updateScore(second = state.v.score.second + 1))
        }
        buttonAccept2.setOnClickListener {
            state.postValue(state.v.copy(step = LIST))
        }
    }

    private fun updateView(state: MatchViewState) {
        // Misc
        imageBall.show(state.step != LIST)

        // Steps
        layoutBid.show(state.step == BID)
        layoutScore.show(state.step == SCORE)
        recyclerView.show(state.step == LIST)

        when (state.step) {
            BID -> {
                textBid.text = "${state.bid} zł"
            }
            SCORE -> {
                textScore1.text = "${state.score.first}"
                textScore2.text = "${state.score.second}"
            }
            LIST -> {
                recyclerView.adapter = SummaryAdapter(listOf(
                        HeaderCell(),
                        SeparatorCell(),
                        EntryCell("Krachtan", Pair(2, 1)),
                        EntryCell("Krystian", Pair(0, 1)),
//                        EntryCell("Bartek", Pair(2, 3)),
                        SeparatorCell(),
                        SummaryCell(10, 30),
                        InviteCell(showText = true)
                ))
                recyclerView.addItemDecoration(ItemDecorator())
            }
        }
    }

    private fun bindMatch(match: Match) {
        date.text = MatchViewHolder.formatter.format(match.date)
        team1.text = getCountryName(match.team1)
        team2.text = getCountryName(match.team2)

        GlideApp.with(flag1.context)
                .load(Uri.parse("file:///android_asset/flags/${match.team1}.png"))
                .centerInside()
                .into(flag1)

        GlideApp.with(flag2.context)
                .load(Uri.parse("file:///android_asset/flags/${match.team2}.png"))
                .centerInside()
                .apply(RequestOptions.circleCropTransform())
                .into(flag2)

        button.visibility = GONE
    }

}


fun <T : Fragment> T.withArgument(arg: Parcelable): T {
    val bundle = Bundle()
    bundle.putParcelable("ARG", arg)
    arguments = bundle
    return this
}


fun <T : Parcelable> bundleOf(arg: T): Bundle {
    val bundle = Bundle()
    bundle.putParcelable("ARG", arg)
    return bundle
}

fun <T : Parcelable> Fragment.getArgument(): T =
        arguments?.getParcelable("ARG")!!


fun View.hide() {
    visibility = GONE
}

fun View.show(visible: Boolean = true) {
    visibility = if (visible) VISIBLE else GONE
}

val <T : Any> MutableLiveData<T>.v
    get() = value!!