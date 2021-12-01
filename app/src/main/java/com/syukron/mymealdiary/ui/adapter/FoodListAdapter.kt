package com.syukron.mymealdiary.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.databinding.ItemFoodBinding


typealias FoodClickListener = (Food) -> Unit
typealias FoodLongClickListener = (PopupMenu, Food, View) -> Boolean

class FoodListAdapter(
    private val clickListener: FoodClickListener? = null,
    private val longClickListener: FoodLongClickListener? = null
) :
    ListAdapter<Food, FoodListAdapter.FoodViewHolder>(DiffCallback) {

    class FoodViewHolder(
        private var binding: ItemFoodBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val resources: Resources? = itemView.resources
        fun bind(food: Food) {
            binding.foodName.text = food.name.replaceFirstChar { it.titlecase() }
            binding.foodCalories.text = resources?.getString(
                R.string.food_calories, food.calories.toFloat()
            )
            binding.servingSize.text = resources?.getString(
                R.string.food_nutrient, food.servingSize.toFloat()
            )
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(
            oldItem: Food,
            newItem: Food
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Food,
            newItem: Food
        ): Boolean {
            return oldItem.servingSize == newItem.servingSize
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {
        val view = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = getItem(position)
        // Sets the item click listener
        if (clickListener != null) {
            holder.itemView.setOnClickListener { clickListener.invoke(food) }
        }
        // Sets the item long click listener
        if (longClickListener != null) {
            holder.itemView.setOnLongClickListener {
                val menu = PopupMenu(it.context, it)
                longClickListener.invoke(menu, food, it)
                menu.show()
                true
            }
        }
        // Calls the holder bind method
        holder.bind(food)
    }
}