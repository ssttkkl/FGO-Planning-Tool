package com.ssttkkl.fgoplanningtool.ui.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import com.ssttkkl.fgoplanningtool.R

class CommonRecViewItemDecoration(context: Context,
                                  val top: Boolean = true,
                                  val bottom: Boolean = true) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
    private val px = context.resources.getDimensionPixelOffset(R.dimen.list_header_and_footer_height)
    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        if (pos == 0 && top)
            outRect.top = px
        if (pos == parent.adapter!!.itemCount - 1 && bottom)
            outRect.bottom = px
    }
}