package com.example.recipeapp.features.dashboard.saved.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemSavedBinding
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel

class SavedAdapter(
    private val onItemClick: (recipeId: Int) -> Unit
) : ListAdapter<RecipeCardUiModel, SavedAdapter.SavedViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SavedViewHolder(ItemSavedBinding.inflate(inflater, parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SavedViewHolder(
        private val binding: ItemSavedBinding,
        private val onItemClick: (recipeId: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeCardUiModel) {
            binding.tvRecipeName.text = item.title
            binding.tvCookingTime.text = itemView.context.getString(
                R.string.home_ready_minutes,
                item.readyInMinutes
            )
            binding.ivRecipeImage.load(item.imageUrl) {
                placeholder(R.drawable.ic_default_image)
                error(R.drawable.ic_default_image)
            }
            binding.ivRecipeImage.contentDescription = itemView.context.getString(
                R.string.home_recipe_image_description,
                item.title
            )
            itemView.setOnClickListener { onItemClick(item.id) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeCardUiModel>() {
            override fun areItemsTheSame(oldItem: RecipeCardUiModel, newItem: RecipeCardUiModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RecipeCardUiModel, newItem: RecipeCardUiModel): Boolean =
                oldItem == newItem
        }
    }
}
