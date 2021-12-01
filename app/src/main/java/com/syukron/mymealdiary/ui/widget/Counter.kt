package com.syukron.mymealdiary.ui.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.databinding.CardCalorieCounterBinding
import com.syukron.mymealdiary.util.DialogWithTextFieldClickListener
import com.syukron.mymealdiary.util.showDialogWithTextField

class Counter(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val binding: CardCalorieCounterBinding = CardCalorieCounterBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun setMoreClickListener(
        hint: String,
        positiveListener: DialogWithTextFieldClickListener? = null
    ) {
        binding.moreIcon.setOnClickListener {
            showDialogWithTextField(
                context,
                title = context.getString(R.string.set_calories_goal),
                hint = hint,
                inputType = InputType.TYPE_CLASS_NUMBER,
                positiveText = context.getString(R.string.save),
                positiveListener = positiveListener
            )
        }
    }

    fun setCalories(calories: Double) {
        binding.caloriesValue.text = formatDouble(calories)
    }

    fun setCaloriesGoal(calories: Int) {
        binding.caloriesGoal.text = calories.toString()
    }

    fun setCaloriesRemaining(calories: Double) {
        binding.caloriesRemaining.text = context.getString(R.string.remaining_value, calories)
        if (calories >= 0) {
            binding.caloriesRemaining.setTextColor(
                ContextCompat.getColor(context, R.color.safe_color)
            )
        } else {
            binding.caloriesRemaining.setTextColor(
                ContextCompat.getColor(context, R.color.danger_color)
            )
        }
    }

    private fun formatDouble(double: Double) = "%.0f".format(double)
}