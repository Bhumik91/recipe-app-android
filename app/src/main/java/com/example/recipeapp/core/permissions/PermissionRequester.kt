package com.example.recipeapp.core.permissions

import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Wraps one ActivityResultLauncher per host Fragment.
 * Register once in onCreate (before STARTED), then call request() from anywhere,
 * including after the Fragment has moved past onCreate.
 */
class PermissionRequester(fragment: Fragment) {

    private var pendingCallback: ((granted: Boolean) -> Unit)? = null

    private val launcher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            pendingCallback?.invoke(granted)
            pendingCallback = null
        }

    fun request(permission: AppPermission, onResult: (granted: Boolean) -> Unit) {
        if (!permission.isApplicable()) {
            onResult(true)
            return
        }
        pendingCallback = onResult
        launcher.launch(permission.manifestPermission)
    }
}
