package com.example.recipeapp.features.recipeDetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemInstructionStepBinding
import com.example.recipeapp.features.recipes.model.StepUiModel

class InstructionStepsAdapter : RecyclerView.Adapter<InstructionStepsAdapter.StepViewHolder>() {

    private val items = mutableListOf<StepUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StepViewHolder(ItemInstructionStepBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(steps: List<StepUiModel>) {
        items.clear()
        items.addAll(steps)
        notifyDataSetChanged()
    }

    class StepViewHolder(
        private val binding: ItemInstructionStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StepUiModel) {
            binding.tvStepNumber.text = itemView.context.getString(
                R.string.recipe_detail_step_number,
                item.number
            )
            binding.tvStepInstruction.text = item.instruction

            if (item.requiredIngredientNames.isEmpty()) {
                binding.tvStepRequiredIngredients.visibility = View.GONE
            } else {
                binding.tvStepRequiredIngredients.visibility = View.VISIBLE
                binding.tvStepRequiredIngredients.text = itemView.context.getString(
                    R.string.recipe_detail_required_ingredients,
                    item.requiredIngredientNames.joinToString(", ")
                )
            }
        }
    }
}
