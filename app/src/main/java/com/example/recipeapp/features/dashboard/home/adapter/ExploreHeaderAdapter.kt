package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemExploreHeaderBinding

class ExploreHeaderAdapter : RecyclerView.Adapter<ExploreHeaderAdapter.ExploreHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreHeaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExploreHeaderViewHolder(ItemExploreHeaderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ExploreHeaderViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = 1

     class ExploreHeaderViewHolder(
        binding: ItemExploreHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
