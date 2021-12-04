package com.syukron.mymealdiary.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.viewmodel.CalculatorViewModel
import com.syukron.mymealdiary.databinding.FragmentCalculatorBinding
import com.syukron.mymealdiary.util.BMRCalculatorUtil

class CalculatorFragment : BaseFragment<FragmentCalculatorBinding>(
    FragmentCalculatorBinding::inflate,
    hasOptionsMenu = true
) {
    private val viewModel: CalculatorViewModel by viewModels()

    private var calculatedBmr: Float = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            calcCalculateBtn.setOnClickListener(calculateButtonListener)
            calcApplyButton.setOnClickListener(applyButtonClickListener)
        }

        viewModel.bmrLiveData.observe(viewLifecycleOwner) { bmr ->
            binding.apply {
                calculatedBmr = bmr.toFloat()
                calcCalculatedBmrTv.visibility = View.VISIBLE
                calcCalculatedBmrTv.text = calculatedBmr.toString() + " ${getString(R.string.kcal)}"
                calcYourBmrTv.visibility = View.VISIBLE
                calcApplyButton.visibility = View.VISIBLE
            }
        }
    }


    private val applyButtonClickListener = View.OnClickListener {
        val newGoal = calculatedBmr.toInt()
        sharedViewModel.setNewCalorieGoal(newGoal)
        Toast.makeText(context,"Calorie Goals has been set !", LENGTH_SHORT).show()
        navigateToTrackerFragment()
    }

    private fun navigateToTrackerFragment() {
        this@CalculatorFragment.findNavController()
            .navigate(R.id.action_calculatorFragment_to_trackerFragment)
    }

    private val calculateButtonListener = View.OnClickListener {
        var validationFailed = validateForm()

        if (!validationFailed) {
            binding.apply {
                viewModel.calculateBMR(
                    gender = calcGenderSpinner.selectedItemPosition,
                    weight = calcWeightIn.text.toString().toFloat(),
                    height = calcHeightIn.text.toString().toFloat(),
                    age = calcAgeIn.text.toString().toInt(),
                    activity = calcActivitySpinner.selectedItemPosition,
                    goal = calcGoalSpinner.selectedItemPosition
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        var validationFailed = false
        binding.apply {
            if (!BMRCalculatorUtil.validateGender(calcGenderSpinner.selectedItemPosition)) {
                (calcGenderSpinner.selectedView as TextView).error =
                    getString(R.string.item_required)
                validationFailed = true
            }
            if (!BMRCalculatorUtil.validateWeight(calcWeightIn.text.toString())) {
                calcWeightIn.error = getString(R.string.item_required)
                validationFailed = true
            }
            if (!BMRCalculatorUtil.validateHeight(calcHeightIn.text.toString())) {
                calcHeightIn.error = getString(R.string.item_required)
                validationFailed = true
            }
            if (!BMRCalculatorUtil.validateAge(calcAgeIn.text.toString())) {
                calcAgeIn.error = getString(R.string.item_required)
                validationFailed = true
            }
            if (!BMRCalculatorUtil.validateActivity(calcActivitySpinner.selectedItemPosition)) {
                (calcActivitySpinner.selectedView as TextView).error =
                    getString(R.string.item_required)
                validationFailed = true
            }
            if (!BMRCalculatorUtil.validateGoal(calcGoalSpinner.selectedItemPosition)) {
                (calcGoalSpinner.selectedView as TextView).error =
                    getString(R.string.item_required)
                validationFailed = true
            }
        }
        return validationFailed
    }
}