package info.czekanski.bet.misc

import android.support.v4.app.*
import info.czekanski.bet.R


fun FragmentActivity.navigateWithTransition(fragment: Fragment, addToBackStack: Boolean = false) {
    supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commitAllowingStateLoss()
}

fun FragmentActivity.navigateWithSlide(fragment: Fragment, addToBackStack: Boolean = false) {
    supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_top, R.anim.fade_out, R.anim.fade_in, R.anim.to_top)
            .replace(R.id.container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commitAllowingStateLoss()
}
