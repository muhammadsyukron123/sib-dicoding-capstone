package com.syukron.mymealdiary.ui

import android.app.Application
import androidx.lifecycle.*
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.data.model.ListType
import com.syukron.mymealdiary.data.repository.FoodRepository
import com.syukron.mymealdiary.util.changeListType
import com.syukron.mymealdiary.util.sum
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

enum class ModType { EDIT, ADD }

class TrackerViewModel(application: Application) :
    AndroidViewModel(application) {

    // Repository
    private val _repository = FoodRepository(application)

    // If the saved foods are from another day, clear the saved foods and save today's date
    init {
        viewModelScope.launch {
            _repository.apply {
                if (!isSavedDateToday()) {
                    clearNonHistoryFoods()
                    saveTodayDateInPreferences()
                }
            }
        }
    }

    // Food Item that is clicked through recyclerViews from SearchFragment or
    // TrackerFragment, and is shown in FoodFragment
    var selectedFood: Food = Food()

    // Whether or not the FoodFragment should add or edit the selected food
    var modType = ModType.EDIT

    // Calories values
    val calories = _repository.getKcalSum()
    val caloriesGoal = MutableLiveData(_repository.getSavedGoalFromPreferences())
    val caloriesRemaining = createRemainingCalories()

    // Which list should the selected food be added to
    private var _listType = ListType.BREAKFAST

    // Food lists
    private val _breakfastList = _repository.getAllFoodsWithListType(ListType.BREAKFAST)
    private val _lunchList = _repository.getAllFoodsWithListType(ListType.LUNCH)
    private val _dinnerList = _repository.getAllFoodsWithListType(ListType.DINNER)
    private val _snacksList = _repository.getAllFoodsWithListType(ListType.SNACKS)
    private val _historyList = _repository.getAllFoodsWithListType(ListType.HISTORY)

    // Public methods

    fun getList(listType: ListType): LiveData<List<Food>> {
        return when (listType) {
            ListType.BREAKFAST -> _breakfastList
            ListType.LUNCH -> _lunchList
            ListType.DINNER -> _dinnerList
            ListType.SNACKS -> _snacksList
            ListType.HISTORY -> _historyList
        }
    }

    fun clearNonHistoryFoods() {
        viewModelScope.launch { _repository.clearNonHistoryFoods() }
    }

    fun clearHistory() {
        viewModelScope.launch { _repository.clearOnlyHistoryFoods() }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch { _repository.deleteFoodFromTheDatabase(food) }
    }

    fun moveFoodToAnotherList(food: Food, newListType: ListType) {
        viewModelScope.launch {
            _repository.deleteFoodFromTheDatabase(food)
            _repository.addFoodToDatabase(food.copy(listType = newListType.ordinal))
        }
    }

    fun setNewCalorieGoal(newCalorieGoal: Int) {
        caloriesGoal.value = newCalorieGoal
        _repository.setSavedGoalFromPreferences(newCalorieGoal)
    }

    fun refreshCalorieGoal() {
        val refreshedCalorieGoal = _repository.getSavedGoalFromPreferences()
        caloriesGoal.value = refreshedCalorieGoal
    }

    suspend fun searchFoodsWithQuery(query: String) {
        _repository.apply {
            val foods = searchFoodsThatMatchQuery(query).changeListType(_listType)
            addFoodsToDatabase(foods)
            addFoodsToHistory(foods)
        }
    }

    fun addFood(food: Food, servingSize: Double) {
        // Id change needed to not cause conflict with foods saved in history
        val foodWithDifferentId = food.copy(id = Random.nextInt()).edit(servingSize, _listType)
        viewModelScope.launch {
            _repository.addFoodToDatabase(foodWithDifferentId)
        }
    }

    fun editFood(food: Food, newServingSize: Double) {
        food.edit(newServingSize, _listType)
        viewModelScope.launch {
            _repository.editFoodFromTheDatabase(food)
        }
    }

    fun setSearchListType(newListType: ListType) {
        _listType = newListType
    }

    fun getNutrientSumOfSavedFoods(): List<Double> {
        val totalNutrition = runBlocking { _repository.getAllExceptHistoryFoods() }.sum()
        return totalNutrition.getNutrients()
    }

    // Private methods

    private fun createRemainingCalories(): MediatorLiveData<Double> {
        // Uses MediatorLiveData to dynamically set the remaining calories of the day
        // (Calories goal - Calories eaten) when any of these two variables change
        return MediatorLiveData<Double>().apply {
            value = 0.0
            val subtract: (x: Double, y: Double) -> Double = { x, y -> x - y }
            addSource(caloriesGoal) { goal ->
                value = subtract(goal.toDouble(), calories.value ?: 0.0)
            }
            addSource(calories) { calories ->
                value = subtract(caloriesGoal.value?.toDouble() ?: 0.0, calories)
            }
        }
    }

    private fun addFoodsToHistory(foodList: List<Food>) {
        val historyList = _historyList.value ?: listOf()
        foodList.map { food ->
            val historyAlreadyContainsThisFood = historyList.any { it.name == food.name }
            if (!historyAlreadyContainsThisFood) {
                viewModelScope.launch {
                    // Adds a copy of the food but with its listType set to HISTORY, and different
                    // ID, so there is no conflict
                    _repository.addFoodToDatabase(
                        food.copy(
                            id = Random.nextInt(),
                            listType = ListType.HISTORY.ordinal
                        )
                    )
                }
            }
        }
    }
}