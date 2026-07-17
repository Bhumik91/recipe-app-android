package com.example.recipeapp.features.dashboard.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.FragmentHomeBinding
import com.example.recipeapp.features.dashboard.DashboardActivity
import com.example.recipeapp.features.dashboard.home.adapter.ChipsAdapter
import com.example.recipeapp.features.dashboard.home.adapter.ExploreHeaderAdapter
import com.example.recipeapp.features.dashboard.home.adapter.ExploreRecipesAdapter
import com.example.recipeapp.features.search.SearchActivity
import com.example.recipeapp.features.dashboard.home.adapter.SavedRecipesAdapter
import com.example.recipeapp.features.dashboard.home.adapter.SavedSectionAdapter
import com.example.recipeapp.features.dashboard.home.ui.HorizontalSpaceItemDecoration
import com.example.recipeapp.features.dashboard.home.ui.VerticalSpaceItemDecoration
import com.example.recipeapp.features.dashboard.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    private val chipsAdapter = ChipsAdapter { cuisine ->
        viewModel.toggleFilter(cuisine)
    }
    private val savedRecipesAdapter = SavedRecipesAdapter()
    private val savedSectionAdapter = SavedSectionAdapter(savedRecipesAdapter)
    private val exploreHeaderAdapter = ExploreHeaderAdapter()
    private val exploreRecipesAdapter = ExploreRecipesAdapter { recipeId ->
        viewModel.onSaveToggled(recipeId)
    }



    private var lastScrollDirectionDown: Boolean? = null

    private val homeScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy == 0) return

            val scrollingDown = dy > 0
            if (lastScrollDirectionDown == scrollingDown) return

            lastScrollDirectionDown = scrollingDown
            (activity as? DashboardActivity)?.updateBottomBarForScroll(scrollingDown)
        }
    }

    private val paginationScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy <= 0) return

            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            if (lastVisibleItem >= totalItemCount - 3) {
                viewModel.loadNextExplorePage()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.tvGreeting.text = getString(R.string.home_greeting_title, viewModel.userName)
        
        setupSearchBar()
        setupChipList()
        setupHomeContent()
        observeUiState()
        viewModel.loadInitial()
    }

    override fun onDestroyView() {
        binding.rvHome.removeOnScrollListener(homeScrollListener)
        binding.rvHome.removeOnScrollListener(paginationScrollListener)
        _binding = null
        lastScrollDirectionDown = null
        super.onDestroyView()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.exploreUiState.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                binding.pbLoading.visibility = View.GONE
                                exploreRecipesAdapter.submitList(state.data)
                            }
                            is UiState.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                            }
                            is UiState.Error -> {
                                binding.pbLoading.visibility = View.GONE
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.savedUiState.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                savedRecipesAdapter.submitList(state.data)
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.selectedCuisine.collect { selected ->
                        chipsAdapter.setSelected(selected)
                    }
                }
            }
        }
    }

    private fun setupSearchBar() {
        binding.svSearch.root.setOnSearchClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupChipList() {
        val chipSpacing = resources.getDimensionPixelSize(R.dimen.spacing_sm)

        binding.rvChips.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = chipsAdapter
            itemAnimator = null

            if (itemDecorationCount == 0) {
                addItemDecoration(
                    HorizontalSpaceItemDecoration(
                        itemSpacing = chipSpacing,
                        edgeSpacing = 0
                    )
                )
            }
        }
        chipsAdapter.submitList(viewModel.getCuisines())
    }


    private fun setupHomeContent() {
        val exploreSpacing = resources.getDimensionPixelSize(R.dimen.spacing_md)

        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ConcatAdapter(savedSectionAdapter, exploreHeaderAdapter, exploreRecipesAdapter)
            itemAnimator = null

            if (itemDecorationCount == 0) {
                addItemDecoration(
                    VerticalSpaceItemDecoration(
                        itemSpacing = exploreSpacing,
                        startPosition = 2
                    )
                )
            }

            removeOnScrollListener(homeScrollListener)
            addOnScrollListener(homeScrollListener)
            removeOnScrollListener(paginationScrollListener)
            addOnScrollListener(paginationScrollListener)
        }
    }
}
