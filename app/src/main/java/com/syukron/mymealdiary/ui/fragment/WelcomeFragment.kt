package com.syukron.mymealdiary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.repository.FoodRepository
import com.syukron.mymealdiary.databinding.FragmentCalculatorBinding
import com.syukron.mymealdiary.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(
    FragmentWelcomeBinding::inflate,
    hasOptionsMenu = true
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val calGoals = sharedViewModel.caloriesGoal.value
        if(calGoals != 0 ) {
            navigateToTrackerFragment()
        }
    }

    private fun navigateToTrackerFragment() {
        this@WelcomeFragment.findNavController()
            .navigate(R.id.action_welcomeFragment_to_trackerFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            welcomeNextBtn.setOnClickListener {
                this@WelcomeFragment.findNavController()
                    .navigate(R.id.action_welcomeFragment_to_calculatorFragment)
            }
        }
    }
}