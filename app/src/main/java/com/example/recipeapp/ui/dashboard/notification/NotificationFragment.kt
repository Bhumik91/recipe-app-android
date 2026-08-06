package com.example.recipeapp.ui.dashboard.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.FragmentNotificationBinding
import com.example.recipeapp.ui.dashboard.DashboardActivity
import com.example.recipeapp.ui.dashboard.notification.adapter.NotificationLogAdapter
import com.example.recipeapp.ui.dashboard.notification.viewmodel.NotificationViewModel
import com.example.recipeapp.ui.recipeDetail.RecipeDetailActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModel()

    private val notificationAdapter by lazy {
        NotificationLogAdapter(onRecipeClicked = ::navigateToRecipeDetail)
    }

    private var lastScrollDirectionDown: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        observeNotificationLogs()
        setupScrollListener()
    }

    private fun setupRecyclerView() {
        binding.rvNotificationLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
    }

    private fun setupTabLayout() {
        binding.tabNotificationFilter.addOnTabSelectedListener(
            object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                    tab?.position?.let { viewModel.selectTab(it) }
                    updateAdapterWithFilteredLogs()
                }

                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            }
        )
    }

    private fun observeNotificationLogs() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logsUiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.pbNotificationLoading.visibility = View.VISIBLE
                            binding.rvNotificationLogs.visibility = View.GONE
                            binding.layoutNotificationEmpty.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.pbNotificationLoading.visibility = View.GONE
                            if (state.data.isEmpty()) {
                                binding.rvNotificationLogs.visibility = View.GONE
                                binding.layoutNotificationEmpty.visibility = View.VISIBLE
                            } else {
                                binding.layoutNotificationEmpty.visibility = View.GONE
                                binding.rvNotificationLogs.visibility = View.VISIBLE
                                updateAdapterWithFilteredLogs()
                            }
                        }
                        is UiState.Error -> {
                            binding.pbNotificationLoading.visibility = View.GONE
                            binding.rvNotificationLogs.visibility = View.GONE
                            binding.layoutNotificationEmpty.visibility = View.VISIBLE
                            binding.tvNotificationEmptyTitle.text = "Error"
                            binding.tvNotificationEmptyBody.text = state.message
                        }
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun updateAdapterWithFilteredLogs() {
        val allLogs = (viewModel.logsUiState.value as? UiState.Success)?.data.orEmpty()
        val filteredLogs = viewModel.getFilteredLogs(allLogs)
        notificationAdapter.submitList(filteredLogs)
    }

    private fun setupScrollListener() {
        binding.scrollNotification.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val dy = scrollY - oldScrollY
            if (dy == 0) return@setOnScrollChangeListener
            val scrollingDown = dy > 0
            if (lastScrollDirectionDown == scrollingDown) return@setOnScrollChangeListener
            lastScrollDirectionDown = scrollingDown
            (activity as? DashboardActivity)?.updateBottomBarForScroll(scrollingDown)
        }
    }

    private fun navigateToRecipeDetail(recipeId: Int) {
        startActivity(RecipeDetailActivity.newIntent(requireContext(), recipeId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lastScrollDirectionDown = null
    }
}
