package com.example.recipeapp.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSearchClickListener: (() -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setOnClickListener {
            onSearchClickListener?.invoke()
        }
    }

    fun setOnSearchClickListener(listener: () -> Unit) {
        onSearchClickListener = listener
    }
}
