package com.example.recipeapp.ui.dashboard.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.domain.recipe.repository.RecipeRepository
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// One-shot UI events (as opposed to savedUiState, which is a replayable state
// snapshot) — a SharedFlow so a Snackbar is never re-shown after a config change.
sealed class SavedSnackbarEvent {
    object ShowUndo : SavedSnackbarEvent()
    data class ShowError(val message: String) : SavedSnackbarEvent()
}

class SavedViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    // --- Observable state exposed to SavedFragment ---

    private val _savedUiState = MutableStateFlow<UiState<List<RecipeCardUiModel>>>(UiState.Idle)
    val savedUiState: StateFlow<UiState<List<RecipeCardUiModel>>> = _savedUiState

    // extraBufferCapacity = 1 so tryEmit() from removeBookmark()/restorePendingRecipe()
    // never silently drops an event if the fragment's collector isn't attached yet
    // (e.g. right after a rotation, before repeatOnLifecycle resubscribes).
    private val _snackbarEvent = MutableSharedFlow<SavedSnackbarEvent>(extraBufferCapacity = 1)
    val snackbarEvent: SharedFlow<SavedSnackbarEvent> = _snackbarEvent

    // --- In-flight optimistic-delete bookkeeping (undo support) ---

    // The delayed network call for the currently-pending removal; cancelled by
    // undoRemoveBookmark() so the delete never actually reaches the repository
    // once the user taps Undo.
    private var pendingDeleteJob: Job? = null
    // The item most recently removed from the list, kept around so it can be
    // spliced back in if the user undoes or the delayed delete call fails.
    private var pendingRemovedRecipe: RecipeCardUiModel? = null
    // Its original index, so undo/restore re-inserts it in the same spot
    // instead of appending it at the end of the list.
    private var pendingRemovedPosition: Int = -1

    // --- Public actions, called by SavedFragment ---

    fun loadSavedRecipes() {
        viewModelScope.launch {
            _savedUiState.value = UiState.Loading
            when (val result = recipeRepository.getSavedRecipes()) {
                is NetworkResult.Success -> {
                    _savedUiState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _savedUiState.value = UiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // Optimistically removes the item from the list immediately (before the network
    // call completes) so the UI feels instant, then waits 3s — matching the Snackbar
    // undo window — before actually persisting the removal. If undoRemoveBookmark()
    // cancels pendingDeleteJob within that window, the repository call never happens.
    fun removeBookmark(recipeId: Int) {
        val current = currentList()
        val position = current.indexOfFirst { it.id == recipeId }
        if (position == -1) return

        // Only one pending removal is tracked at a time; a second removal while one
        // is already in flight cancels the first rather than stacking undo state.
        pendingDeleteJob?.cancel()

        val recipe = current[position]
        pendingRemovedRecipe = recipe
        pendingRemovedPosition = position

        _savedUiState.value = UiState.Success(current.toMutableList().apply { removeAt(position) })
        _snackbarEvent.tryEmit(SavedSnackbarEvent.ShowUndo)

        pendingDeleteJob = viewModelScope.launch {
            delay(3000)
            when (val result = recipeRepository.removeSavedRecipe(recipe.id)) {
                is NetworkResult.Error -> restorePendingRecipe(result.message)
                else -> clearPendingRecipe()
            }
        }
    }

    fun undoRemoveBookmark() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        restorePendingRecipe(errorMessage = null)
    }

    // --- Private helpers ---

    // Shared by both undo (errorMessage = null, no Snackbar) and a failed delayed
    // delete (errorMessage set, shows ShowError) — both cases re-insert the item
    // at its original position and clear the pending-removal bookkeeping.
    private fun restorePendingRecipe(errorMessage: String?) {
        val recipe = pendingRemovedRecipe ?: return
        val insertAt = pendingRemovedPosition.coerceIn(0, currentList().size)

        _savedUiState.value = UiState.Success(
            currentList().toMutableList().apply { add(insertAt, recipe) }
        )
        clearPendingRecipe()

        if (errorMessage != null) {
            _snackbarEvent.tryEmit(SavedSnackbarEvent.ShowError(errorMessage))
        }
    }

    private fun clearPendingRecipe() {
        pendingRemovedRecipe = null
        pendingRemovedPosition = -1
        pendingDeleteJob = null
    }

    // savedUiState only ever holds a real list while in the Success case; any other
    // state (Idle/Loading/Error) has nothing to splice an item in or out of.
    private fun currentList(): List<RecipeCardUiModel> =
        (_savedUiState.value as? UiState.Success<List<RecipeCardUiModel>>)?.data.orEmpty()
}
