package com.example.recipeapp.features.dashboard.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeapp.R

class NotificationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lastScrollDirectionDown: Boolean? = null
        val scrollView = view as androidx.core.widget.NestedScrollView
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val dy = scrollY - oldScrollY
            if (dy == 0) return@setOnScrollChangeListener
            val scrollingDown = dy > 0
            if (lastScrollDirectionDown == scrollingDown) return@setOnScrollChangeListener
            lastScrollDirectionDown = scrollingDown
            (activity as? com.example.recipeapp.features.dashboard.DashboardActivity)?.updateBottomBarForScroll(scrollingDown)
        }
    }
}
