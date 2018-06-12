package info.czekanski.bet.network.firebase.model

data class FirebaseBet(
        val id: String = "",
        val state: String = STATE_OPEN,
        val matchId: String = "",
        val users: Map<String, Boolean> = mapOf(),
        val bets: Map<String, FirebaseBetEntry> = mapOf()
) {

    companion object {
        const val STATE_OPEN = "OPEN"
        const val STATE_ACTIVE = "ACTIVE"
        const val STATE_CLOSED = "CLOSED"
    }
}