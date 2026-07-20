package com.example.recipeapp.common.gesture

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// Reusable swipe-to-delete gesture: attach to any RecyclerView with a single call
// instead of wiring an ItemTouchHelper.SimpleCallback per screen.
object SwipeToDeleteCallback {

    fun addGesture(recyclerView: RecyclerView, onSwiped: (position: Int) -> Unit) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                onSwiped(position)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }
}
