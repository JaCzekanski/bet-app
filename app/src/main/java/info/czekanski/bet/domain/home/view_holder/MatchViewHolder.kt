package info.czekanski.bet.domain.home.view_holder

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.request.RequestOptions
import info.czekanski.bet.domain.home.Callback
import kotlinx.android.extensions.LayoutContainer
import info.czekanski.bet.domain.home.cells.MatchCell
import info.czekanski.bet.misc.GlideApp
import kotlinx.android.synthetic.main.layout_match.*
import java.text.SimpleDateFormat
import java.util.*


class MatchViewHolder(override val containerView: View, val callback: Callback) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(cell: MatchCell) {
        with(cell) {
            date.text = formatter.format(match.date)
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

            button.setOnClickListener {
                callback(cell)
            }
        }
    }

    companion object {
        val formatter = SimpleDateFormat("H:mm  .  d.MM", Locale.US)

        fun getCountryName(code: String): String {
            return Locale("", code.toUpperCase()).displayCountry
        }
    }
}