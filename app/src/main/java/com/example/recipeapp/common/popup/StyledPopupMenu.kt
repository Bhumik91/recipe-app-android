package com.example.recipeapp.common.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.recipeapp.R

class StyledPopupMenu(private val context: Context, private val anchor: View) {

    private val popupWindow: PopupWindow
    private val container: LinearLayout
    private var onMenuItemClickListener: ((Int) -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(context)
        val contentView = inflater.inflate(R.layout.layout_custom_popup_menu, null)
        container = contentView.findViewById(R.id.menu_items_container)

        popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 10f
            isOutsideTouchable = true
            isFocusable = true
            // Setting background to null as the CardView handles the background and elevation
            setBackgroundDrawable(null)
        }
    }

    fun addMenuItem(itemId: Int, title: String, iconRes: Int? = null, iconTint: Int? = null) {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.layout_popup_menu_item, container, false)

        val titleView = itemView.findViewById<TextView>(R.id.menu_item_title)
        val iconView = itemView.findViewById<ImageView>(R.id.menu_item_icon)

        titleView.text = title
        if (iconRes != null) {
            iconView.setImageResource(iconRes)
            iconView.visibility = View.VISIBLE
            iconTint?.let { iconView.setColorFilter(it) }
        } else {
            iconView.visibility = View.GONE
        }

        itemView.setOnClickListener {
            onMenuItemClickListener?.invoke(itemId)
            dismiss()
        }

        container.addView(itemView)
    }

    fun setOnMenuItemClickListener(listener: (Int) -> Unit) {
        onMenuItemClickListener = listener
    }

    fun show() {
        // Show at the end of the anchor
        popupWindow.showAsDropDown(anchor, -(160 * context.resources.displayMetrics.density).toInt() + anchor.width, 0)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}
