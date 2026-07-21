package com.example.recipeapp.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemSearchBinding
import com.example.recipeapp.data.recipes.uimodel.SearchRecipeUiModel

class SearchGridAdapter(
    private val onItemClick: (recipeId: Int) -> Unit
) : ListAdapter<SearchRecipeUiModel, SearchGridAdapter.SearchGridViewHolder>(SearchRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchGridViewHolder(ItemSearchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SearchGridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchGridViewHolder(
        private val binding: ItemSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchRecipeUiModel) {
            binding.tvTitle.text = item.title
            binding.ivRecipe.load(item.imageUrl) {
                placeholder(R.drawable.ic_default_image)
                error(R.drawable.ic_default_image)
            }
            binding.ivRecipe.contentDescription = itemView.context.getString(
                R.string.home_recipe_image_description,
                item.title
            )
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                onItemClick(getItem(position).id)
            }
        }
    }

    class SearchRecipeDiffCallback : DiffUtil.ItemCallback<SearchRecipeUiModel>() {
        override fun areItemsTheSame(oldItem: SearchRecipeUiModel, newItem: SearchRecipeUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchRecipeUiModel, newItem: SearchRecipeUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
