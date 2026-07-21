package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recipeapp.core.session.SessionStorage
import com.example.recipeapp.features.dashboard.DashboardActivity
import com.example.recipeapp.features.onboarding.OnBoardingActivity
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val sessionManager: SessionStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = if (!sessionManager.isLoggedIn) {
            Intent(this, OnBoardingActivity::class.java)
        } else {
            Intent(this, DashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}