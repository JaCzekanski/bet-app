package info.czekanski.bet.misc

import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.show(visible: Boolean = true) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

val View.visible: Boolean
    get() = visibility == View.VISIBLE


val viewMap = mutableMapOf<View, Boolean>()

fun View.delayedShow(visible: Boolean = true) {
    viewMap[this] = visible
}

fun commitDelayedShow() {
    // First hide view
    viewMap.filter { !it.value }.keys.forEach { v -> if (v.visible) v.hide() }

    // Then show
    viewMap.filter { it.value }.keys.forEach { v -> if (!v.visible) v.show() }

    // Clear references
    viewMap.clear()
}