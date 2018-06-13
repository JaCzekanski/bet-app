package info.czekanski.bet.network

fun String.scoreToPair(): Pair<Int, Int>? {
    val split = split(":")
    if (split.size != 2) {
        return null
    }

    return Pair(split[0].toInt(), split[1].toInt())
}