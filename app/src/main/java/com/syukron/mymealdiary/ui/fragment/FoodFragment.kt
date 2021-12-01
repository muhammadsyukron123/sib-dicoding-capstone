package com.syukron.mymealdiary.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.databinding.FoodNutrientViewBinding
import com.syukron.mymealdiary.ui.ModType
import com.syukron.mymealdiary.util.showDialogWithTextField

class FoodFragment : BaseFragment<FoodNutrientViewBinding>(
    FoodNutrientViewBinding::inflate,
    lockDrawer = true,
    hasOptionsMenu = true
) {

    private lateinit var selectedFood: Food

    override fun applyBinding(v: View): ApplyTo<FoodNutrientViewBinding> = {
        selectedFood = sharedViewModel.selectedFood

        val nutrients = makeNutrients(selectedFood)
        nutrition.makeAdapter(nutrients, true, showDialog())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.food_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done_button -> {
                setAddButtonClickListener(binding, sharedViewModel.modType)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAddButtonClickListener(
        binding: FoodNutrientViewBinding,
        modType: ModType
    ) {
        val newServingSize = binding.nutrition.getFirstNutrientValue()
        if (modType == ModType.ADD)
            sharedViewModel.addFood(selectedFood, newServingSize)
        else {
            sharedViewModel.editFood(selectedFood, newServingSize)
        }
        this@FoodFragment.findNavController()
            .navigate(R.id.action_foodFragment_to_trackerFragment)
    }

    private fun makeNutrients(food: Food): MutableList<Double> {
        val nutrients = mutableListOf<Double>()
        nutrients.add(food.servingSize.toDouble())
        nutrients.addAll(food.getNutrients())
        return nutrients
    }

    private fun showDialog(): (View) -> Unit = {
        showDialogWithTextField(
            requireContext(),
            title = resources.getString(R.string.serving_size),
            hint = resources.getString(
                R.string.food_nutrient,
                selectedFood.servingSize.toDouble()
            ),
            positiveText = resources.getString(R.string.save),
            positiveListener = { _, _, servingEditText ->
                val newServingSize = servingEditText.text.toString().toDouble()
                val newFood = selectedFood.copy().edit(newServingSize)
                val nutrients = makeNutrients(newFood)
                binding.nutrition.makeAdapter(
                    nutrients, true,
                    showDialog(), true
                )
            }
        )
    }
}