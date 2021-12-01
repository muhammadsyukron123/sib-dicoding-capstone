package com.syukron.mymealdiary.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.data.model.ListType
import com.syukron.mymealdiary.databinding.FragmentTrackerBinding
import com.syukron.mymealdiary.ui.ModType
import com.syukron.mymealdiary.ui.adapter.FoodListAdapter
import com.syukron.mymealdiary.ui.widget.FoodListView

class TrackerFragment : BaseFragment<FragmentTrackerBinding>(
    FragmentTrackerBinding::inflate,
    hasOptionsMenu = true
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedViewModel.modType = ModType.EDIT
        sharedViewModel.refreshCalorieGoal()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun applyBinding(v: View): ApplyTo<FragmentTrackerBinding> = {
        counter.apply {
            setOnClickListener { navigateToNutrients() }
            sharedViewModel.caloriesGoal.observe(viewLifecycleOwner) { goal ->
                setCaloriesGoal(goal)
                setMoreClickListener(goal.toString()) { _, _, editText ->
                    val newGoal = editText.text.toString().toInt()
                    sharedViewModel.setNewCalorieGoal(newGoal)
                }
            }
            sharedViewModel.calories.observe(viewLifecycleOwner) { calories ->
                setCalories(calories)
            }
            sharedViewModel.caloriesRemaining.observe(viewLifecycleOwner) { remaining ->
                setCaloriesRemaining(remaining)
            }
        }
        applyFoodListView(breakfast, ListType.BREAKFAST)
        applyFoodListView(lunch, ListType.LUNCH)
        applyFoodListView(dinner, ListType.DINNER)
        applyFoodListView(snacks, ListType.SNACKS)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bar_button -> {
                navigateToNutrients()
                true
            }
            R.id.clear_button -> {
                sharedViewModel.clearNonHistoryFoods()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clickListener(listType: ListType): (Food) -> (Unit) = { food ->
        sharedViewModel.apply {
            selectedFood = food
            setSearchListType(listType)
        }
        val argument = bundleOf(
            "foodName" to food.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        )
        this@TrackerFragment.findNavController()
            .navigate(R.id.action_trackerFragment_to_foodFragment, argument)
    }

    private val longClickListener: (PopupMenu, Food, View) -> (Boolean) =
        { menu, food, view ->
            menu.inflate(R.menu.tracker_options_menu)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove_from_list -> sharedViewModel.deleteFood(food)
                    R.id.move_to -> {
                        val moveMenu = PopupMenu(view.context, view)
                        moveMenu.inflate(R.menu.move_to_menu)
                        moveMenu.setOnMenuItemClickListener { moveItem ->
                            when (moveItem.itemId) {
                                R.id.move_to_breakfast ->
                                    sharedViewModel.moveFoodToAnotherList(
                                        food,
                                        ListType.BREAKFAST
                                    )
                                R.id.move_to_lunch ->
                                    sharedViewModel.moveFoodToAnotherList(
                                        food,
                                        ListType.LUNCH
                                    )
                                R.id.move_to_dinner ->
                                    sharedViewModel.moveFoodToAnotherList(
                                        food,
                                        ListType.DINNER
                                    )
                                R.id.move_to_snacks ->
                                    sharedViewModel.moveFoodToAnotherList(
                                        food,
                                        ListType.SNACKS
                                    )
                            }
                            true
                        }
                        moveMenu.show()
                    }
                }
                true
            }
            true
        }

    private fun applyFoodListView(
        foodListView: FoodListView,
        listType: ListType
    ) {
        foodListView.apply {
            setButtonClickListener {
                navigateToSearch(listType)
            }
            val listTitle = listType.toString().lowercase().replaceFirstChar { it.titlecase() }
            setListTitle(listTitle)
            sharedViewModel.getList(listType)
                .observe(viewLifecycleOwner) { list ->
                    setListData(list)
                }
            setAdapter(
                FoodListAdapter(
                    clickListener(listType),
                    longClickListener
                )
            )
        }
    }

    private fun navigateToNutrients() {
        val argument = bundleOf("upButtonNeeded" to true)
        this@TrackerFragment
            .findNavController()
            .navigate(
                R.id.action_trackerFragment_to_nutrientFragment,
                argument
            )
    }

    private fun navigateToSearch(listType: ListType) {
        sharedViewModel.setSearchListType(listType)
        findNavController()
            .navigate(R.id.action_trackerFragment_to_searchFragment)
    }
}