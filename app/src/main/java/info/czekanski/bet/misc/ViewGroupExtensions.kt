package info.czekanski.bet.misc

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.v7.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun ViewGroup.inflate(@LayoutRes layout: Int): View =
        LayoutInflater.from(context).inflate(layout, this, false)


fun ViewGroup.inflateWithStyle(@StyleRes style: Int, @LayoutRes layout: Int, attachToRoot: Boolean = true): View {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val inflater = layoutInflater.cloneInContext(ContextThemeWrapper(context, style))
    return inflater.inflate(layout, this, attachToRoot)
}