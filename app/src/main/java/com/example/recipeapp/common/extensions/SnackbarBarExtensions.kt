package com.example.recipeapp.common.extensions

import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.recipeapp.R
import com.google.android.material.snackbar.Snackbar

fun Snackbar.applyAppTheme(): Snackbar {
    val context = this.view.context
    // Snackbar background
    this.view.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray_4)
    val poppinsFont = ResourcesCompat.getFont(context, R.font.poppins_regular)
    // Snack bar text
    val textView = this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView?.let {
        it.typeface = poppinsFont
       it.setTextColor(Color.BLACK)
    }
    // Action Text
    val actionButton = this.view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)
    actionButton?.let {
        it.typeface = poppinsFont
    }
    this.setActionTextColor(ContextCompat.getColor(context, R.color.primary)) // Sets action tint

    return this
}