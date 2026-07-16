package com.example.recipeapp.features.dashboard.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentHomeBinding
import com.example.recipeapp.features.dashboard.DashboardActivity
import com.example.recipeapp.features.dashboard.home.adapter.ChipsAdapter
import com.example.recipeapp.features.dashboard.home.adapter.ExploreHeaderAdapter
import com.example.recipeapp.features.dashboard.home.adapter.ExploreRecipesAdapter
import com.example.recipeapp.features.dashboard.home.adapter.SavedRecipesAdapter
import com.example.recipeapp.features.dashboard.home.adapter.SavedSectionAdapter
import com.example.recipeapp.features.dashboard.home.model.HomeDummyData
import com.example.recipeapp.features.dashboard.home.ui.HorizontalSpaceItemDecoration
import com.example.recipeapp.features.dashboard.home.ui.VerticalSpaceItemDecoration

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val chipsAdapter = ChipsAdapter()
    private val savedRecipesAdapter = SavedRecipesAdapter()
    private val savedSectionAdapter = SavedSectionAdapter(savedRecipesAdapter)
    private val exploreHeaderAdapter = ExploreHeaderAdapter()
    private val exploreRecipesAdapter = ExploreRecipesAdapter()

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
        setupSearchView()
        setupChipList()
        setupHomeContent()
    }

    override fun onDestroyView() {
        binding.homeRecyclerView.removeOnScrollListener(homeScrollListener)
        _binding = null
        lastScrollDirectionDown = null
        super.onDestroyView()
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            queryHint = getString(R.string.home_search_hint)
            maxWidth = Int.MAX_VALUE
            isIconified = false
            clearFocus()
        }

        binding.searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)?.background = null

        binding.searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            ?.apply {
                typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_1))
                setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_3))
            }

        val iconTint = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.gray_3)
        )
        binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            ?.imageTintList = iconTint
        binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            ?.imageTintList = iconTint
    }

    private fun setupChipList() {
        val chipSpacing = resources.getDimensionPixelSize(R.dimen.spacing_sm)

        binding.chipsRecyclerView.apply {
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

        chipsAdapter.submitList(HomeDummyData.getCuisineChips(requireContext()))
    }

    private fun setupHomeContent() {
        val exploreSpacing = resources.getDimensionPixelSize(R.dimen.spacing_md)

        savedRecipesAdapter.submitList(HomeDummyData.getSavedRecipes(requireContext()))
        exploreRecipesAdapter.submitList(HomeDummyData.getExploreRecipes(requireContext()))

        binding.homeRecyclerView.apply {
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
        }
    }
}
