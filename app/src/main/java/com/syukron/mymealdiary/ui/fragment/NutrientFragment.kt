package com.syukron.mymealdiary.ui.fragment

import android.view.View
import com.syukron.mymealdiary.databinding.FoodNutrientViewBinding

class NutrientFragment :
    BaseFragment<FoodNutrientViewBinding>(
        FoodNutrientViewBinding::inflate,
        topLevelAndCanHaveUpButton = true
    ) {

    override fun applyBinding(v: View): ApplyTo<FoodNutrientViewBinding> = {
        val nutrients = sharedViewModel.getNutrientSumOfSavedFoods()
        nutrition.makeAdapter(nutrients)
    }
}
