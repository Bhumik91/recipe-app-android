package com.example.recipeapp.features.dashboard

import android.os.Bundle
import com.example.recipeapp.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipeapp.databinding.ActivityDashboardBinding
import com.google.android.material.bottomappbar.BottomAppBar

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (isKeyboardVisible) {
                binding.babMain.visibility = android.view.View.GONE
                binding.fabAdd.hide()
            } else {
                binding.babMain.visibility = android.view.View.VISIBLE
                binding.fabAdd.show()
            }
            insets
        }
        
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_nav_host) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        androidx.navigation.ui.NavigationUI.setupWithNavController(binding.bnvMain, navController)
    }

    fun updateBottomBarForScroll(scrollingDown: Boolean) {
        val params = binding.babMain.layoutParams as? CoordinatorLayout.LayoutParams ?: return
        val behavior = params.behavior as? BottomAppBar.Behavior ?: return

        if (scrollingDown) {
            behavior.slideDown(binding.babMain)
            binding.fabAdd.hide()
        } else {
            behavior.slideUp(binding.babMain)
            binding.fabAdd.show()
        }
    }
}
