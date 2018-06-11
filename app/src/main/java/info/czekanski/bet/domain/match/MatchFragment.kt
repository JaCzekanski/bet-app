package info.czekanski.bet.domain.match

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import info.czekanski.bet.R
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder
import info.czekanski.bet.domain.home.view_holder.MatchViewHolder.Companion.getCountryName
import info.czekanski.bet.misc.GlideApp
import info.czekanski.bet.model.Match
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.android.synthetic.main.layout_match.*

class MatchFragment : Fragment() {
    val match by lazy { getArgument<Match>() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        bindMatch(match)
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

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager
                    .popBackStack()
        }
    }


    companion object {
        fun newInstance(match: Match) = MatchFragment().withArgument(match)
    }
}


fun <T : Fragment> T.withArgument(arg: Parcelable): T {
    val bundle = Bundle()
    bundle.putParcelable("ARG", arg)
    arguments = bundle
    return this
}

fun <T : Parcelable> Fragment.getArgument(): T =
        arguments?.getParcelable("ARG")!!
