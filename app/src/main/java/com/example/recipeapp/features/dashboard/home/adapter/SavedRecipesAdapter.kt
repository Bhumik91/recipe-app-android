package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemSavedRecipeBinding
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel

class SavedRecipesAdapter : RecyclerView.Adapter<SavedRecipesAdapter.SavedRecipeViewHolder>() {

    private val items = mutableListOf<RecipeCardUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SavedRecipeViewHolder(ItemSavedRecipeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SavedRecipeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(recipes: List<RecipeCardUiModel>) {
        items.clear()
        items.addAll(recipes)
        notifyDataSetChanged()
    }

     class SavedRecipeViewHolder(
        private val binding: ItemSavedRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeCardUiModel) {
            binding.titleTextView.text = item.title
            binding.timeTextView.text = itemView.context.getString(
                R.string.home_ready_minutes,
                item.readyInMinutes
            )
            binding.recipeImageView.setImageResource(item.imageRes)
            binding.recipeImageView.contentDescription = itemView.context.getString(
                R.string.home_recipe_image_description,
                item.title
            )
            binding.bookmarkImageView.contentDescription = itemView.context.getString(
                R.string.home_saved_recipe_content_description,
                item.title
            )
        }
    }
}
