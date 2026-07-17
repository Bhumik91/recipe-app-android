package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemChipBinding

class ChipsAdapter(
    private val onChipClicked: (String) -> Unit
) : RecyclerView.Adapter<ChipsAdapter.ChipViewHolder>() {

    private val items = mutableListOf<String>()
    private var selectedCuisine: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChipViewHolder(ItemChipBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(cuisines: List<String>) {
        items.clear()
        items.addAll(cuisines)
        notifyDataSetChanged()
    }

    fun setSelected(cuisine: String?) {
        selectedCuisine = cuisine
        notifyDataSetChanged()
    }

    inner class ChipViewHolder(
        private val binding: ItemChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cuisine: String) {
            binding.chipItem.text = cuisine
            binding.chipItem.isChecked = cuisine == selectedCuisine || (cuisine == "All" && selectedCuisine == null)
            binding.chipItem.setOnClickListener {
                onChipClicked(cuisine)
            }
        }
    }
}
