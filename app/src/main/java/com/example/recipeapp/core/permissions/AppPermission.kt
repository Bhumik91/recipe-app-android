package com.example.recipeapp.core.permissions

import android.Manifest
import android.os.Build

/**
 * Single source of truth for every runtime permission the app can request.
 * To add a new permission (camera, location, etc.) add one entry here —
 * nothing else in this file needs to change.
 */
enum class AppPermission(
    val manifestPermission: String,
    val minSdk: Int = Build.VERSION_CODES.BASE,
    val rationaleTitle: String,
    val rationaleMessage: String
) {
    NOTIFICATIONS(
        manifestPermission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU, // 33 — no-op below this
        rationaleTitle = "Stay updated",
        rationaleMessage = "Allow notifications so we can let you know when a recipe is saved or removed."
    );

    /** True if this permission doesn't apply on the running OS version (e.g. POST_NOTIFICATIONS pre-33). */
    fun isApplicable(): Boolean = Build.VERSION.SDK_INT >= minSdk
}
