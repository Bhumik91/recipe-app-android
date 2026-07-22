package com.example.recipeapp.ui.dashboard.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.storage.notificationlog.NotificationLogEntity
import com.example.recipeapp.storage.notificationlog.NotificationLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationLogRepository: NotificationLogRepository
) : ViewModel() {

    private val _logsUiState = MutableStateFlow<UiState<List<NotificationLogEntity>>>(UiState.Loading)
    val logsUiState: StateFlow<UiState<List<NotificationLogEntity>>> = _logsUiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0=All, 1=Saved, 2=Removed
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        loadNotificationLogs()
    }

    private fun loadNotificationLogs() {
        viewModelScope.launch {
            try {
                notificationLogRepository.observeLogs().collect { logs ->
                    _logsUiState.value = if (logs.isEmpty()) {
                        UiState.Success(emptyList())
                    } else {
                        UiState.Success(logs)
                    }
                }
            } catch (e: Exception) {
                _logsUiState.value = UiState.Error(e.message ?: "Failed to load logs")
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Returns logs filtered by the current tab selection
    fun getFilteredLogs(allLogs: List<NotificationLogEntity>): List<NotificationLogEntity> {
        return when (_selectedTab.value) {
            0 -> allLogs // All
            1 -> allLogs.filter { it.action == RecipeAction.SAVED } // Saved
            2 -> allLogs.filter { it.action == RecipeAction.REMOVED } // Removed
            else -> allLogs
        }
    }
}
