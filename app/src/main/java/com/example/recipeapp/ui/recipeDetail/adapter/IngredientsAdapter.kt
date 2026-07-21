package com.example.recipeapp.ui.recipeDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemIngredientBinding
import com.example.recipeapp.data.recipes.uimodel.IngredientUiModel

class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    private val items = mutableListOf<IngredientUiModel>()
    private var baseServings: Int = 1
    private var targetServings: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientViewHolder(ItemIngredientBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(items[position], baseServings, targetServings)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(ingredients: List<IngredientUiModel>, baseServings: Int) {
        items.clear()
        items.addAll(ingredients)
        this.baseServings = baseServings
        this.targetServings = baseServings
        notifyDataSetChanged()
    }

    fun updateTargetServings(targetServings: Int) {
        this.targetServings = targetServings
        notifyDataSetChanged()
    }

    class IngredientViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IngredientUiModel, baseServings: Int, targetServings: Int) {
            binding.tvIngredientName.text = item.name
            binding.tvIngredientAmount.text = item.displayAmount(baseServings, targetServings)
            binding.ivIngredient.load(item.imageUrl) {
                placeholder(R.drawable.ic_default_image)
                error(R.drawable.ic_default_image)
            }
        }
    }
}
