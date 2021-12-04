package com.syukron.mymealdiary.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.viewmodel.CalculatorViewModel
import com.syukron.mymealdiary.databinding.FragmentCalculatorBinding
import com.syukron.mymealdiary.util.BMRCalculatorUtil
import javax.inject.Inject

const val BMR_PREF = "BMR"

class CalculatorFragment : Fragment() {

    private var _binding : FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalculatorViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private var calculatedBmr: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        sharedPrefs.edit().putFloat(BMR_PREF, calculatedBmr).apply()
        navigateToDailyFragment()
    }

    private fun navigateToDailyFragment() {
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