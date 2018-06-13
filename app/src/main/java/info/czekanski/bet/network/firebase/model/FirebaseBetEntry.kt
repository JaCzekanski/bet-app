package info.czekanski.bet.network.firebase.model

import java.util.*

data class FirebaseBetEntry(
        val bid: Int? = null,
        val date: Date = Date(),
        val score: String = ""
)