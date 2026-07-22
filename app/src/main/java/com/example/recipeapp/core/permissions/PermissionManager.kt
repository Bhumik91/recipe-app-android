package com.example.recipeapp.core.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Interface that allows us to check permission status without coupling
 * to UI classes like Activity or Fragment. This makes it safe to inject
 * into ViewModels, Repositories, etc.
 */
interface PermissionManager {
    fun isGranted(permission: AppPermission): Boolean
}

/**
 * Concrete implementation of [PermissionManager] that utilizes standard Android Context APIs.
 */
class PermissionManagerImpl(private val context: Context) : PermissionManager {
    override fun isGranted(permission: AppPermission): Boolean {
        if (!permission.isApplicable()) return true // treat as granted, nothing to ask for
        return ContextCompat.checkSelfPermission(
            context,
            permission.manifestPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
