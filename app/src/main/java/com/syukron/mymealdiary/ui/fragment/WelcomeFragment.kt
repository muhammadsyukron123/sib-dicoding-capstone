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
        if (sharedViewModel.caloriesGoal.equals(0)){
            navigateToTrackerFragment()
        }
    }

    private var _binding: FragmentWelcomeBinding? = null
//    private override val binding get() = _binding!!

    private fun navigateToTrackerFragment() {
        this@WelcomeFragment.findNavController()
            .navigate(R.id.action_welcomeFragment_to_trackerFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
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