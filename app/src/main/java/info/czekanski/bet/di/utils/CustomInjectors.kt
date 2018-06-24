package info.czekanski.bet.di.utils

import android.support.v7.widget.RecyclerView
import android.view.View

object CustomInjectors {
    @JvmStatic
    fun inject(view: View) {
        val application = view.context.applicationContext
        if (application !is HasViewInjector) {
            throw RuntimeException(String.format(
                    "%s does not implement %s",
                    application.javaClass.canonicalName,
                    HasViewInjector::class.java.canonicalName))
        }

        val viewInjection = (application as HasViewInjector).viewInjector()
        checkNotNull(viewInjection, { "%s.viewInjector() returned null".format(application.javaClass) })

        viewInjection.inject(view)
    }

    @JvmStatic
    fun inject(viewHolder: RecyclerView.ViewHolder) {
//        val application = viewHolder.itemView.context.applicationContext
//        if (application !is HasViewHolderInjector) {
//            throw RuntimeException(String.format(
//                    "%s does not implement %s",
//                    application.javaClass.canonicalName,
//                    HasViewHolderInjector::class.java.canonicalName))
//        }
//
//        val viewHolderInjector = (application as HasViewHolderInjector).viewHolderInjector()
//        checkNotNull(viewHolderInjector, { "%s.viewHolderInjector() returned null".format(application.javaClass) })
//
//        viewHolderInjector.inject(viewHolder)
    }
}