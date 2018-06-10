package info.czekanski.bet.domain.matches.view_holder

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_match.*
import info.czekanski.bet.domain.matches.cells.MatchCell
import info.czekanski.bet.misc.GlideApp
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.os.ConfigurationCompat.getLocales
import android.os.Build
import kotlinx.android.synthetic.main.activity_main.*


class MatchViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

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
        }
    }

    private fun getCountryName(code: String): String {
        return Locale("", code.toUpperCase()).displayCountry
    }

    companion object {
        val formatter = SimpleDateFormat("H:mm  .  d.MM", Locale.US)
    }
}