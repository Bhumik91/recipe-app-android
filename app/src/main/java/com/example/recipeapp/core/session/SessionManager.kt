package com.example.recipeapp.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.recipeapp.features.auth.model.LoginResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_LOGGED_IN, value) }

    fun saveAuthSession(response: LoginResponse) {
        prefs.edit().apply {
            putInt(KEY_ID, response.id)
            putString(KEY_NAME, response.name)
            putString(KEY_ACCESS_TOKEN, response.accessToken)
            putString(KEY_REFRESH_TOKEN, response.refreshToken)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserId(): Int = prefs.getInt(KEY_ID, 0)
    fun getUserName(): String = prefs.getString(KEY_NAME, "") ?: ""

    fun clearSession() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREF_NAME = "RecipeAppSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ID = "userId"
        private const val KEY_NAME = "userName"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
    }
}