package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.dashboard.DashBoardActivity
import com.example.recipeapp.features.onboarding.OnBoardingActivity
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!sessionManager.isLoggedIn) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
            return
        }
        startActivity(Intent(this, DashBoardActivity::class.java))
        finish()
    }
}