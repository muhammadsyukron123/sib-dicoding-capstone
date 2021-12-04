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

    private val _repository = FoodRepository(application)

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

    var selectedFood: Food = Food()

    var modType = ModType.EDIT

    val calories = _repository.getKcalSum()
    val caloriesGoal = MutableLiveData(_repository.getSavedGoalFromPreferences(0))
    val caloriesRemaining = createRemainingCalories()

    private var _listType = ListType.BREAKFAST

    private val _breakfastList = _repository.getAllFoodsWithListType(ListType.BREAKFAST)
    private val _lunchList = _repository.getAllFoodsWithListType(ListType.LUNCH)
    private val _dinnerList = _repository.getAllFoodsWithListType(ListType.DINNER)
    private val _snacksList = _repository.getAllFoodsWithListType(ListType.SNACKS)
    private val _historyList = _repository.getAllFoodsWithListType(ListType.HISTORY)

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

    fun refreshCalorieGoal(goal : Int) {
        val refreshedCalorieGoal = _repository.getSavedGoalFromPreferences(goal)
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


    private fun createRemainingCalories(): MediatorLiveData<Double> {
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