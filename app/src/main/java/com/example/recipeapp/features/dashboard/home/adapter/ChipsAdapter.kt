package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemChipBinding
import com.example.recipeapp.features.dashboard.home.model.ChipUiModel

class ChipsAdapter : RecyclerView.Adapter<ChipsAdapter.ChipViewHolder>() {

    private val items = mutableListOf<ChipUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChipViewHolder(ItemChipBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(chips: List<ChipUiModel>) {
        items.clear()
        items.addAll(chips)
        notifyDataSetChanged()
    }

    inner class ChipViewHolder(
        private val binding: ItemChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChipUiModel) {
            binding.chip.text = item.label
            binding.chip.isChecked = item.isSelected
            binding.chip.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                val currentItem = items[position]
                items[position] = currentItem.copy(isSelected = !currentItem.isSelected)
                notifyItemChanged(position)
            }
        }
    }
}
