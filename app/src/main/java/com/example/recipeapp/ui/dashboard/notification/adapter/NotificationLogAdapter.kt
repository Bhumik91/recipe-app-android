package com.example.recipeapp.ui.dashboard.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.databinding.ItemNotificationLogBinding
import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.storage.notificationlog.NotificationLogEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationLogAdapter(
    private val onRecipeClicked: (recipeId: Int) -> Unit
) : ListAdapter<NotificationLogEntity, NotificationLogAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, onRecipeClicked)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationLogBinding,
        private val onRecipeClicked: (recipeId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: NotificationLogEntity) {
            val context = binding.root.context

            // Recipe name
            binding.tvRecipeName.text = log.recipeName

            // Action message
            val actionText = "${log.recipeName} ${if (log.action == RecipeAction.SAVED) "added to" else "removed from"} Saved Recipes."
            binding.tvActionMessage.text = actionText

            // Smart date/time formatting
            binding.tvDateTime.text = formatDateTime(log.timestamp)

            // Action badge with conditional styling
            setupActionBadge(context, log.action)

            // Click listener
            binding.root.setOnClickListener {
                onRecipeClicked(log.recipeId)
            }
        }

        private fun setupActionBadge(context: Context, action: RecipeAction) {
            binding.ivActionBadge.setBackgroundResource(R.drawable.bg_icon_circle)
            if (action == RecipeAction.SAVED) {
                binding.ivActionBadge.background.setTint(
                    ContextCompat.getColor(context, R.color.primary_40)
                )
                binding.ivActionBadge.setImageResource(R.drawable.ic_saved_outlined)
                binding.ivActionBadge.setColorFilter(
                    ContextCompat.getColor(context, R.color.primary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.ivActionBadge.background.setTint(
                    ContextCompat.getColor(context, R.color.warning_light)
                )
                binding.ivActionBadge.setImageResource(R.drawable.ic_trashed_outlined)
                binding.ivActionBadge.setColorFilter(
                    ContextCompat.getColor(context, R.color.warning),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        private fun formatDateTime(timestamp: Long): String {
            val logDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = sdf.format(Date(timestamp))

            return when {
                // Today
                logDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                logDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                    "Today, $time"
                }
                // Yesterday
                logDate.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) &&
                logDate.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) -> {
                    "Yesterday, $time"
                }
                // Within 7 days (show weekday name)
                daysAgo(timestamp) in 2..6 -> {
                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    "${dayFormat.format(Date(timestamp))}, $time"
                }
                // After 7 days (show DD-MM)
                else -> {
                    val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
                    "${dateFormat.format(Date(timestamp))}, $time"
                }
            }
        }

        private fun daysAgo(timestamp: Long): Int {
            val now = Calendar.getInstance()
            val logDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            val diff = now.timeInMillis - logDate.timeInMillis
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationLogEntity>() {
        override fun areItemsTheSame(oldItem: NotificationLogEntity, newItem: NotificationLogEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NotificationLogEntity, newItem: NotificationLogEntity) =
            oldItem == newItem
    }
}
