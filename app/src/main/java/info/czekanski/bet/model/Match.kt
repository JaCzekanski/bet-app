package info.czekanski.bet.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Match(
        val id: String = "",
        val team1: Team = "",
        val team2: Team = "",
        val date: Date = Date(),
        val event: String? = null,
        val score: String? = null
) : Parcelable
typealias Team = String