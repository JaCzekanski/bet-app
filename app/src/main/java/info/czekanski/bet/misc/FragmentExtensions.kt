package info.czekanski.bet.misc

import android.os.*
import android.support.v4.app.Fragment


fun <T : Fragment> T.withArgument(arg: Parcelable): T {
    val bundle = Bundle()
    bundle.putParcelable("ARG", arg)
    arguments = bundle
    return this
}


fun <T : Parcelable> Fragment.getArgument(): T =
        arguments?.getParcelable("ARG")!!

