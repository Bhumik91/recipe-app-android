package com.example.recipeapp.ui.dashboard.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.common.extensions.applyAppTheme
import com.example.recipeapp.common.gesture.SwipeToDeleteCallback
import com.example.recipeapp.common.itemdecor.VerticalSpaceItemDecoration
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.FragmentSavedBinding
import com.example.recipeapp.ui.dashboard.DashboardActivity
import com.example.recipeapp.ui.dashboard.saved.adapter.SavedAdapter
import com.example.recipeapp.ui.dashboard.saved.viewmodel.SavedSnackbarEvent
import com.example.recipeapp.ui.dashboard.saved.viewmodel.SavedViewModel
import com.example.recipeapp.ui.recipeDetail.RecipeDetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedFragment : Fragment() {

    // Nullable var, not lateinit — set in onCreateView(), cleared in onDestroyView().
    // A Fragment instance can outlive its View (e.g. sitting on the back stack), so
    // this needs to be nullable to release the View tree between the two; lateinit
    // would either leak the old View or throw on the first access after teardown.
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedViewModel by viewModel()

    // Constructed once with fixed callbacks rather than per-onViewCreated, since the
    // lambdas only reference viewModel/requireContext() (both fragment-scoped, not
    // view-scoped) and don't need to be recreated when the view is torn down.
    private val savedAdapter = SavedAdapter(
        onItemClick = { recipeId -> openRecipeDetail(recipeId) }
    )

    // Tracks the last scroll direction so the bottom bar is only toggled on an
    // actual direction change, not on every onScrolled callback.
    private var lastScrollDirectionDown: Boolean? = null

    private val savedScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy == 0) return

            val scrollingDown = dy > 0
            if (lastScrollDirectionDown == scrollingDown) return

            lastScrollDirectionDown = scrollingDown
            (activity as? DashboardActivity)?.updateBottomBarForScroll(scrollingDown)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSavedList()
        observeUiState()
        observeSnackbarEvents()
        // Retry re-enters Loading and re-triggers the same API call — no separate
        // "retry" method needed on the ViewModel since it's just loadSavedRecipes() again.
        binding.tvRetry.setOnClickListener { viewModel.loadSavedRecipes() }
        viewModel.loadSavedRecipes()
    }

    override fun onDestroyView() {
        /* Scroll listener holds a reference to the RecyclerView; remove it explicitly
        before nulling the binding so it can't fire against a torn-down view.*/
        binding.rvSaved.removeOnScrollListener(savedScrollListener)
        _binding = null
        lastScrollDirectionDown = null
        super.onDestroyView()
    }

    private fun openRecipeDetail(recipeId: Int) {
        startActivity(RecipeDetailActivity.newIntent(requireContext(), recipeId))
    }

    // --- UI setup ---

    private fun setupSavedList() {
        binding.rvSaved.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = savedAdapter

            // Only add the decoration once, on first setup — onViewCreated fires every
            // time the view is re-created (e.g. after rotation), but setupSavedList()
            // is called fresh each time with a new RecyclerView instance.
            if (itemDecorationCount == 0) {
                addItemDecoration(VerticalSpaceItemDecoration(itemSpacing = 30))
            }

            // Re-attach scroll listener on every onViewCreated since the RecyclerView
            // view instance is new; the listener is fragment-scoped (not view-scoped)
            // so it persists across rotations and would try to fire against a dead
            // view if we didn't remove + re-add it here.
            removeOnScrollListener(savedScrollListener)
            addOnScrollListener(savedScrollListener)
        }

        // Attaching an ItemTouchHelper is idempotent — attachToRecyclerView() on a
        // fresh RecyclerView instance each onViewCreated is safe and matches the
        // scroll-listener re-attach pattern above.
        SwipeToDeleteCallback.addGesture(binding.rvSaved) { position ->
            val recipeId = savedAdapter.currentList[position].id
            viewModel.removeBookmark(recipeId)
        }
    }

    // --- Observable collectors (resubscribe via repeatOnLifecycle on every STARTED) ---

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedUiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.rvSaved.visibility = View.INVISIBLE
                            binding.pbSavedLoading.visibility = View.VISIBLE
                            binding.errorStateContainer.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            // submitList() auto-diffs and animates only the changed rows
                            // (via SavedAdapter's ListAdapter + DIFF_CALLBACK).
                            savedAdapter.submitList(state.data)
                            binding.pbSavedLoading.visibility = View.GONE
                            binding.rvSaved.visibility = View.VISIBLE
                            binding.errorStateContainer.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.pbSavedLoading.visibility = View.GONE
                            binding.rvSaved.visibility = View.GONE
                            binding.errorStateContainer.visibility = View.VISIBLE
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeSnackbarEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.snackbarEvent.collect { event ->
                    when (event) {
                        is SavedSnackbarEvent.ShowUndo -> showUndoSnackbar()
                        is SavedSnackbarEvent.ShowError -> showErrorSnackbar(event.message)
                    }
                }
            }
        }
    }
    // --- UI helpers (Snackbars) ---

    private fun showUndoSnackbar() {
        val dashboardActivity = activity as? DashboardActivity
        // Must show the bottom bar first so setAnchorView() positions the Snackbar
        // above a visible bar; if the bar is hidden, the Snackbar would anchor to
        // nothing and fall off-screen.
        dashboardActivity?.showBottomBar()
        Snackbar.make(binding.root, R.string.saved_recipe_removed_message, 3000)
            // Anchor to the activity's BottomAppBar instead of this fragment's root
            // CoordinatorLayout — two separate view hierarchies, so a Snackbar made
            // from the fragment's root can't find the bar otherwise.
            .setAnchorView(dashboardActivity?.bottomAppBar)
            .setAction(R.string.saved_recipe_undo_action) { viewModel.undoRemoveBookmark() }
            .applyAppTheme()
            .show()
    }

    private fun showErrorSnackbar(message: String) {
        val dashboardActivity = activity as? DashboardActivity
        dashboardActivity?.showBottomBar()
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAnchorView(dashboardActivity?.bottomAppBar)
            .applyAppTheme()
            .show()
    }
}
