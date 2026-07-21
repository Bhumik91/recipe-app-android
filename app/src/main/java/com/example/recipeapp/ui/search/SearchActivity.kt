package com.example.recipeapp.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.common.filter.DietFilterBottomSheet
import com.example.recipeapp.common.itemdecor.GridSpacingItemDecoration
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.ActivitySearchBinding
import com.example.recipeapp.ui.recipeDetail.RecipeDetailActivity
import com.example.recipeapp.ui.search.adapter.SearchGridAdapter
import com.example.recipeapp.ui.search.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private val searchGridAdapter = SearchGridAdapter(
        onItemClick = { recipeId ->
            startActivity(RecipeDetailActivity.newIntent(this, recipeId))
        }
    )

    private val recentSearchesAdapter = SearchGridAdapter(
        onItemClick = { recipeId ->
            startActivity(RecipeDetailActivity.newIntent(this, recipeId))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerViews()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerViews() {
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_sm)

        binding.rvSearchResults.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = searchGridAdapter
            addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        }

        binding.rvRecentSearches.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = recentSearchesAdapter
            addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            }
        })

        // Pressing the keyboard's "search" action should submit the query and dismiss the
        // keyboard to reveal the results, instead of the default behavior of inserting a newline.
        binding.etSearch.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onSearchQueryChanged(view.text?.toString().orEmpty())
                hideKeyboard()
                view.clearFocus()
                true
            } else {
                false
            }
        }

        binding.btnFilter.setOnClickListener {
            DietFilterBottomSheet.show(supportFragmentManager, viewModel.selectedDiets.value)
        }

        supportFragmentManager.setFragmentResultListener(
            DietFilterBottomSheet.REQUEST_KEY,
            this
        ) { _, bundle ->
            val selectedDiets = bundle.getStringArrayList(DietFilterBottomSheet.RESULT_SELECTED_DIETS).orEmpty()
            viewModel.applyDietFilter(selectedDiets)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchResults.collect { state ->
                        when (state) {
                            is UiState.Idle -> {
                                binding.pbSearchLoading.visibility = View.GONE
                                binding.rvSearchResults.visibility = View.GONE
                                binding.tvResultsCount.visibility = View.GONE
                                binding.tvNoResults.visibility = View.GONE
                                showRecentSearches(true)
                            }
                            is UiState.Loading -> {
                                binding.pbSearchLoading.visibility = View.VISIBLE
                                binding.rvSearchResults.visibility = View.GONE
                                binding.tvResultsCount.visibility = View.GONE
                                binding.tvNoResults.visibility = View.GONE
                                showRecentSearches(false)
                            }
                            is UiState.Success -> {
                                binding.pbSearchLoading.visibility = View.GONE
                                showRecentSearches(false)
                                if (state.data.isEmpty()) {
                                    binding.rvSearchResults.visibility = View.GONE
                                    binding.tvResultsCount.visibility = View.GONE
                                    binding.tvNoResults.visibility = View.VISIBLE
                                    binding.tvNoResults.text = getString(
                                        R.string.search_no_results,
                                        viewModel.searchQuery.value
                                    )
                                } else {
                                    binding.tvNoResults.visibility = View.GONE
                                    binding.tvResultsCount.visibility = View.VISIBLE
                                    binding.tvResultsCount.text = getString(
                                        R.string.search_results_count,
                                        state.data.size
                                    )
                                    binding.rvSearchResults.visibility = View.VISIBLE
                                    searchGridAdapter.submitList(state.data)
                                }
                            }
                            is UiState.Error -> {
                                binding.pbSearchLoading.visibility = View.GONE
                                binding.rvSearchResults.visibility = View.GONE
                                binding.tvResultsCount.visibility = View.GONE
                                binding.tvNoResults.visibility = View.VISIBLE
                                binding.tvNoResults.text = state.message
                                showRecentSearches(false)
                            }
                        }
                    }
                }

                launch {
                    viewModel.recentSearches.collect { recentSearches ->
                        recentSearchesAdapter.submitList(recentSearches)
                        // Only reveal the recent-searches section while idle and if there's
                        // anything to show; re-apply visibility now the list has arrived.
                        if (viewModel.searchResults.value is UiState.Idle) {
                            showRecentSearches(true)
                        }
                    }
                }

                launch {
                    // The filter only makes sense once a search has actually been run, so it
                    // stays disabled/grey until the user hits search, then lights up.
                    viewModel.searchResults.collect { state ->
                        val filterEnabled = state !is UiState.Idle
                        binding.btnFilter.isEnabled = filterEnabled
                        binding.btnFilter.setBackgroundResource(
                            if (filterEnabled) R.drawable.bg_filter_button else R.drawable.bg_filter_button_disabled
                        )
                    }
                }

                launch {
                    viewModel.selectedDiets.collect { diets ->
                        binding.viewFilterActiveDot.visibility =
                            if (diets.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun showRecentSearches(show: Boolean) {
        val hasRecentSearches = viewModel.recentSearches.value.isNotEmpty()
        val visibility = if (show && hasRecentSearches) View.VISIBLE else View.GONE
        binding.tvRecentLabel.visibility = visibility
        binding.rvRecentSearches.visibility = visibility
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}
