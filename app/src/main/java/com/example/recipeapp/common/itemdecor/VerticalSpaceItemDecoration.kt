package com.example.recipeapp.common.itemdecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    private val itemSpacing: Int,
    // Only needed when the RecyclerView's adapter is a ConcatAdapter and earlier
    // adapter positions (headers/other sections) shouldn't receive this spacing —
    // e.g. HomeFragment passes a lambda that adds up its saved-section + explore-header
    // adapters' *current* item counts, since the saved section's count toggles between
    // 0 and 1 at runtime. A fixed Int here would go stale the moment that count changes,
    // misaligning spacing by one position (see the HomeFragment usage for why).
    private val startPosition: () -> Int = { 0 },
    // Optional top margin for the item at startPosition, so the first decorated item
    // isn't flush against whatever comes before it.
    private val firstItemTopSpacing: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val start = startPosition()
        if (position == RecyclerView.NO_POSITION || position < start) return

        if (position == start) {
            outRect.top = firstItemTopSpacing
        }
        outRect.bottom = itemSpacing
    }
}
