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

    // Exposed so fragments hosted in this Activity's nav graph can anchor Snackbars
    // above the bottom bar — a Snackbar made from a fragment's own CoordinatorLayout
    // can't reach this bar otherwise, since the two live in separate view hierarchies.
    val bottomAppBar: BottomAppBar
        get() = binding.babMain

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

    // --- Public API: called by hosted fragments' scroll listeners / snackbar flows ---

    fun updateBottomBarForScroll(scrollingDown: Boolean) {
        if (scrollingDown) hideBottomBar() else showBottomBar()
    }

    // Public (not private like hideBottomBar) because SavedFragment calls this
    // directly before showing a Snackbar — the bar must already be visible for
    // setAnchorView() to position the Snackbar above it correctly.
    fun showBottomBar() {
        val behavior = bottomBarBehavior() ?: return
        behavior.slideUp(binding.babMain)
        binding.fabAdd.show()
    }

    // --- Private helpers ---

    private fun hideBottomBar() {
        val behavior = bottomBarBehavior() ?: return
        behavior.slideDown(binding.babMain)
        binding.fabAdd.hide()
    }

    private fun bottomBarBehavior(): BottomAppBar.Behavior? {
        val params = binding.babMain.layoutParams as? CoordinatorLayout.LayoutParams ?: return null
        return params.behavior as? BottomAppBar.Behavior
    }
}
