package info.czekanski.bet.misc

import android.view.*


fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.show(visible: Boolean = true) {
    visibility = if (visible) View.VISIBLE else View.GONE
}