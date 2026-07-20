package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemChipBinding
import com.example.recipeapp.features.recipes.model.FilterOption

// Horizontal cuisine-chip row shown under the search bar on Home. Backed by ListAdapter so
// re-submitting the list after a selection change (see HomeViewModel.toggleFilter) only
// re-binds the chips whose checked state actually differs, instead of a full
// notifyDataSetChanged() rebind of every chip on every toggle.
class ChipsAdapter(
    private val onChipClicked: (String) -> Unit
) : ListAdapter<FilterOption, ChipsAdapter.ChipViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChipViewHolder(ItemChipBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChipViewHolder(
        private val binding: ItemChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Each RecyclerView holder owns an independent Chip instance, so — unlike
        // DietFilterBottomSheet, which inflates the same item_chip.xml directly into a
        // ChipGroup — there's no shared-view-id lookup involved here; isChecked is set
        // straight from the bound FilterOption on every bind (including recycled views).
        fun bind(option: FilterOption) {
            binding.chipItem.text = option.label
            binding.chipItem.isChecked = option.isSelected
            binding.chipItem.setOnClickListener {
                onChipClicked(option.label)
            }
        }
    }

    companion object {
        // Chips are diffed by label (identity) and full equality (content) so DiffUtil can
        // tell "same chip, different checked state" apart from "different chip entirely".
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilterOption>() {
            override fun areItemsTheSame(oldItem: FilterOption, newItem: FilterOption) =
                oldItem.label == newItem.label

            override fun areContentsTheSame(oldItem: FilterOption, newItem: FilterOption) =
                oldItem == newItem
        }
    }
}
