package info.czekanski.bet.repository

class NicknameCache {
    val map = mutableMapOf<String, String?>()

    companion object {
        val instance by lazy {
            NicknameCache()
        }
    }
}