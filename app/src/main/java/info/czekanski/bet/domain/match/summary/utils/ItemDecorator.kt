package info.czekanski.bet.domain.home.utils

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import info.czekanski.bet.misc.dpToPx

class ItemDecorator : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = 32.dpToPx(view.context)
        outRect.right = 32.dpToPx(view.context)
    }
}
