package com.syukron.mymealdiary.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.model.Nutrient
import com.syukron.mymealdiary.databinding.ItemNutrientBinding


private const val MICRONUTRIENTS = 3

class NutrientAdapter(
    private val nutrients: List<Nutrient>,
    private val isListFromIndividualFood: Boolean = false,
    private val clickListener: View.OnClickListener? = null
) :
    RecyclerView.Adapter<NutrientAdapter.NutrientViewHolder>() {

    fun getFirstElementValue(): Double = nutrients.first().second

    class NutrientViewHolder(
        private var binding: ItemNutrientBinding,
        private val micronutrients: List<Nutrient>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val resources: Resources? = itemView.resources

        fun bind(nutrient: Nutrient, isCalorie: Boolean = false) {
            val nutrientString = when {
                micronutrients.contains(nutrient) -> {
                    R.string.food_micronutrient
                }
                isCalorie -> {
                    R.string.food_calories
                }
                else -> {
                    R.string.food_nutrient
                }
            }

            binding.nutritionName.text = nutrient.first
            binding.nutritionSize.text = resources?.getString(nutrientString, nutrient.second)
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NutrientViewHolder {
        val view = ItemNutrientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NutrientViewHolder(view, nutrients.takeLast(MICRONUTRIENTS))
    }

    override fun onBindViewHolder(holder: NutrientViewHolder, position: Int) {
        val nutrient = nutrients[position]
        when {
            isListFromIndividualFood -> {
                when (position) {
                    0 -> {
                        holder.itemView.setOnClickListener(clickListener)
                        holder.bind(nutrient)
                    }
                    1 -> holder.bind(nutrient, isCalorie = true)
                    else -> holder.bind(nutrient)
                }
            }
            position == 0 -> {
                holder.bind(nutrient, isCalorie = true)
            }
            else -> {
                holder.bind(nutrient)
            }
        }
    }
}