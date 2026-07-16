package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemRecipeBinding
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel

class ExploreRecipesAdapter : RecyclerView.Adapter<ExploreRecipesAdapter.ExploreRecipeViewHolder>() {

    private val items = mutableListOf<RecipeCardUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreRecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExploreRecipeViewHolder(ItemRecipeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ExploreRecipeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(recipes: List<RecipeCardUiModel>) {
        items.clear()
        items.addAll(recipes)
        notifyDataSetChanged()
    }

    inner class ExploreRecipeViewHolder(
        private val binding: ItemRecipeBinding
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
            bindBookmark(item)
        }

        private fun bindBookmark(item: RecipeCardUiModel) {
            binding.bookmarkButton.setImageResource(
                if (item.isSaved) R.drawable.ic_saved_filled else R.drawable.ic_saved_outlined
            )
            binding.bookmarkButton.contentDescription = itemView.context.getString(
                R.string.home_toggle_saved_content_description,
                item.title
            )
            binding.bookmarkButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                val currentItem = items[position]
                items[position] = currentItem.copy(isSaved = !currentItem.isSaved)
                notifyItemChanged(position)
            }
        }
    }
}
