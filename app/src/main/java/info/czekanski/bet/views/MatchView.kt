package info.czekanski.bet.views

import android.content.*
import android.support.v4.content.*
import android.support.v7.view.ContextThemeWrapper
import android.util.*
import android.view.*
import android.widget.*
import info.czekanski.bet.*
import info.czekanski.bet.misc.*
import info.czekanski.bet.model.*
import info.czekanski.bet.network.*
import kotlinx.android.synthetic.main.view_match.view.*
import java.text.*
import java.util.*

class MatchView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(ContextThemeWrapper(context, R.style.MatchView), attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_match, this)
    }

    fun bindMatch(match: Match, callback: ButtonCallback? = null, userScore: String? = null) {
        date.text = formatter.format(match.date)
        team1.text = getCountryName(match.team1)
        team2.text = getCountryName(match.team2)

        GlideApp.with(flag1.context)
                .load(android.net.Uri.parse("file:///android_asset/flags/${match.team1}.png"))
                .centerInside()
                .into(flag1)

        GlideApp.with(flag2.context)
                .load(android.net.Uri.parse("file:///android_asset/flags/${match.team2}.png"))
                .centerInside()
                .apply(com.bumptech.glide.request.RequestOptions.circleCropTransform())
                .into(flag2)

        if (match.state == MatchState.BEFORE) {
            if (callback == null) {
                button.hide()
            } else {
                button.show()
                button.setOnClickListener { callback() }
            }
        } else {
            button.hide()
        }

        val gameScore = match.score?.scoreToPair() ?: kotlin.Pair(0, 0)

        score.setTextColor(ContextCompat.getColor(context, if (match.score != null) R.color.textActive else R.color.textInactive))
        score.text = "%d - %d".format(gameScore.first, gameScore.second)

        if (userScore != null) {
            myScore.show()
            myScore.text = userScore
        } else {
            when (match.state) {
                MatchState.DURING -> {
                    myScore.show()
                    myScore.text = "TRWA"
                }
                MatchState.AFTER -> {
                    myScore.show()
                    myScore.text = "KONIEC"
                }
                else -> {
                    myScore.hide()
                }
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

typealias ButtonCallback = () -> Unit