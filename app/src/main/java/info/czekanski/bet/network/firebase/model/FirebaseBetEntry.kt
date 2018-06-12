package info.czekanski.bet.network.firebase.model

import java.util.*

data class FirebaseBetEntry(
        val bid: Int? = null,
        val date: Date = Date(),
        val score: String = ""
) {
    fun scoreToPair():Pair<Int, Int>? {
        val split = score.split(":")
        if (split.size != 2) {
            return null
        }

        return Pair(split[0].toInt(), split[1].toInt())
    }
}