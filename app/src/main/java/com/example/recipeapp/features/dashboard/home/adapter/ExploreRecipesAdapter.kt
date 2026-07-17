package com.example.recipeapp.features.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemRecipeBinding
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel

class ExploreRecipesAdapter(
    private val onSaveClick: (recipeId: Int) -> Unit
) : RecyclerView.Adapter<ExploreRecipesAdapter.ExploreRecipeViewHolder>() {

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
            binding.tvTitle.text = item.title
            binding.tvTime.text = itemView.context.getString(
                R.string.home_ready_minutes,
                item.readyInMinutes
            )
            binding.ivRecipe.load(item.imageUrl) {
                placeholder(R.drawable.ic_default_image)
                error(R.drawable.ic_default_image)
            }
            binding.ivRecipe.contentDescription = itemView.context.getString(
                R.string.home_recipe_image_description,
                item.title
            )
            bindSave(item)
        }

        private fun bindSave(item: RecipeCardUiModel) {
            binding.btnSave.setImageResource(
                if (item.isSaved) R.drawable.ic_saved_filled else R.drawable.ic_saved_outlined
            )
            binding.btnSave.contentDescription = itemView.context.getString(
                R.string.home_toggle_saved_content_description,
                item.title
            )
            binding.btnSave.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                onSaveClick(items[position].id)
            }
        }
    }
}
