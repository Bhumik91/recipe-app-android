package com.example.recipeapp.ui.dashboard.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import com.example.recipeapp.common.itemdecor.VerticalSpaceItemDecoration
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.DialogLogoutConfirmationBinding
import com.example.recipeapp.databinding.FragmentProfileBinding
import com.example.recipeapp.data.auth.UserDetailsDto
import com.example.recipeapp.ui.dashboard.DashboardActivity
import com.example.recipeapp.ui.dashboard.profile.viewmodel.ProfileViewModel
import com.example.recipeapp.ui.dashboard.saved.adapter.SavedAdapter
import com.example.recipeapp.ui.recipeDetail.RecipeDetailActivity
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.google.android.material.tabs.TabLayout
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModel()

    private val recipesAdapter = SavedAdapter(
        onItemClick = { recipeId -> openRecipeDetail(recipeId) }
    )

    private var selectedTab = 0
    private var latestRecipesState: UiState<List<RecipeCardUiModel>> = UiState.Idle

    // Handles tab selection changes and updates content accordingly
    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            selectedTab = tab.position
            renderTabContent()
        }
        override fun onTabUnselected(tab: TabLayout.Tab) = Unit
        override fun onTabReselected(tab: TabLayout.Tab) = Unit
    }

    private var lastScrollDirectionDown: Boolean? = null

    // Notifies the parent activity to hide/show the bottom navigation bar on scroll
    private val scrollListener =
        NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val dy = scrollY - oldScrollY
            if (dy == 0) return@OnScrollChangeListener

            val scrollingDown = dy > 0
            if (lastScrollDirectionDown == scrollingDown) return@OnScrollChangeListener

            lastScrollDirectionDown = scrollingDown
            (activity as? DashboardActivity)?.updateBottomBarForScroll(scrollingDown)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set up UI state observers to react to data changes from the ViewModel.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureOnClicks()
        setupRecipesList()
        setupTabs()
        observeUiState()
        observeRecipesUiState()
        binding.scrollProfile.setOnScrollChangeListener(scrollListener)
    }

    override fun onDestroyView() {
        binding.scrollProfile.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
        binding.tabProfileSections.removeOnTabSelectedListener(tabSelectedListener)
        _binding = null
        lastScrollDirectionDown = null
        super.onDestroyView()
    }

    private fun configureOnClicks() {
        binding.toolbarProfile.inflateMenu(R.menu.menu_profile)
        binding.toolbarProfile.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                showLogoutConfirmationDialog()
                true
            } else {
                false
            }
        }
    }

    private fun openRecipeDetail(recipeId: Int) {
        startActivity(RecipeDetailActivity.newIntent(requireContext(), recipeId))
    }

    private fun setupRecipesList() {
        binding.rvProfileRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipesAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(VerticalSpaceItemDecoration(itemSpacing = 30))
            }
        }
    }

    private fun setupTabs() {
        binding.tabProfileSections.removeOnTabSelectedListener(tabSelectedListener)
        binding.tabProfileSections.addOnTabSelectedListener(tabSelectedListener)
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBinding = DialogLogoutConfirmationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnLogout.setOnClickListener {
            dialog.dismiss()
            performLogout()
        }

        dialog.show()
    }

    private fun performLogout() {
        viewModel.logout()
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.pbProfileLoading.visibility = View.VISIBLE
                            binding.scrollProfile.visibility = View.INVISIBLE
                        }
                        is UiState.Success -> {
                            binding.pbProfileLoading.visibility = View.GONE
                            binding.scrollProfile.visibility = View.VISIBLE
                            bindUser(state.data)
                        }
                        is UiState.Error -> {
                            binding.pbProfileLoading.visibility = View.GONE
                            binding.scrollProfile.visibility = View.VISIBLE
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    /**
     * Binds user data to the UI components.
     */
    private fun bindUser(user: UserDetailsDto) {
        val displayName = listOf(user.firstName, user.lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { user.userName }

        binding.tvDisplayName.text = displayName
        binding.tvUsername.text = getString(R.string.profile_username, user.userName)
        binding.tvEmail.text = user.email
        binding.ivAvatar.load(user.imageUrl) {
            placeholder(R.drawable.ic_default_image)
            error(R.drawable.ic_default_image)
        }
    }

    private fun observeRecipesUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recipesUiState.collect { state ->
                    latestRecipesState = state
                    if (selectedTab == 0) renderRecipesTab(state)
                }
            }
        }
    }

    /**
     * Handles content rendering based on the currently selected tab.
     */
    private fun renderTabContent() {
        when (selectedTab) {
            0 -> renderRecipesTab(latestRecipesState)
            1 -> showEmptyState(
                R.string.profile_empty_videos_title,
                getString(R.string.profile_empty_videos_body)
            )
            else -> showEmptyState(
                R.string.profile_empty_tag_title,
                getString(R.string.profile_empty_tag_body)
            )
        }
    }

    private fun renderRecipesTab(state: UiState<List<RecipeCardUiModel>>) {
        when (state) {
            is UiState.Success -> {
                recipesAdapter.submitList(state.data)
                binding.rvProfileRecipes.visibility = View.VISIBLE
                binding.layoutProfileTabEmpty.visibility = View.GONE
            }
            is UiState.Error -> showEmptyState(R.string.error_something_went_wrong, state.message)
            else -> {
                binding.rvProfileRecipes.visibility = View.GONE
                binding.layoutProfileTabEmpty.visibility = View.GONE
            }
        }
    }

    private fun showEmptyState(@StringRes titleRes: Int, body: String) {
        binding.rvProfileRecipes.visibility = View.GONE
        binding.layoutProfileTabEmpty.visibility = View.VISIBLE
        binding.tvProfileTabEmptyTitle.setText(titleRes)
        binding.tvProfileTabEmptyBody.text = body
    }
}
