package com.example.recipeapp.common.itemdecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(
    private val itemSpacing: Int,
    private val edgeSpacing: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        outRect.left = if (position == 0) edgeSpacing else itemSpacing
        outRect.right = if (position == state.itemCount - 1) edgeSpacing else 0
    }
}
