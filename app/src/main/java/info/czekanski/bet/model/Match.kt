package info.czekanski.bet.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Match(
        val team1: Team = "",
        val team2: Team = "",
        val date: Date = Date()
) : Parcelable
typealias Team = String