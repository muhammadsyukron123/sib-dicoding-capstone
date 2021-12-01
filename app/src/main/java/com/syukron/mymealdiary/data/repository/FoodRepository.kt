package com.syukron.mymealdiary.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.data.RemoteDataSource
import com.syukron.mymealdiary.data.database.FoodDatabase
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.data.model.ListType
import com.syukron.mymealdiary.util.ConnectionChecker
import com.syukron.mymealdiary.util.FoodNotFoundException
import com.syukron.mymealdiary.util.NoConnectionException
import java.util.*

class FoodRepository(val context: Context) {
    private val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)

    private val apiPreferencesKey = context.getString(R.string.api_preferences_key)
    private val datePreferencesKey = context.getString(R.string.date_preferences_key)
    private val goalPreferencesKey = context.getString(R.string.goal_preferences_key)

    private val localDataSource = FoodDatabase.getInstance(context).foodDao
    private val remoteDataSource = RemoteDataSource.httpClient

    private val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    private val defaultApiKey = context.getString(R.string.api_key)
    private val apiKey =
        sharedPreferences.getString(apiPreferencesKey, defaultApiKey) ?: defaultApiKey

    suspend fun searchFoodsThatMatchQuery(query: String): List<Food> {
        if (!ConnectionChecker.isOnline()) throw NoConnectionException()
        val list = remoteDataSource.getFoodList(apiKey, query).items
        if (list.isEmpty()) throw FoodNotFoundException()
        return list
    }

    suspend fun addFoodsToDatabase(foods: List<Food>) {
        localDataSource.insertFoods(foods)
    }

    suspend fun addFoodToDatabase(food: Food) {
        localDataSource.insertFood(food)
    }

    suspend fun deleteFoodFromTheDatabase(food: Food) {
        localDataSource.deleteFood(food)
    }

    suspend fun editFoodFromTheDatabase(food: Food): Food {
        localDataSource.updateFood(food)
        return food
    }

    suspend fun clearNonHistoryFoods() {
        localDataSource.clearAllExceptHistoryFoods()
    }

    suspend fun clearOnlyHistoryFoods() {
        localDataSource.clearOnlyHistoryFoods()
    }

    suspend fun getAllExceptHistoryFoods(): List<Food> {
        return localDataSource.getAllExceptHistoryFoods()
    }

    fun getAllFoodsWithListType(listType: ListType): LiveData<List<Food>> {
        return localDataSource.getAllFoodsWithListType(listType.ordinal)
    }

    fun getSavedGoalFromPreferences(): Int {
        val savedGoal = sharedPreferences.getString(
            goalPreferencesKey,
            "2000"
        )
        return savedGoal?.toInt() ?: 2000
    }

    fun setSavedGoalFromPreferences(newSavedGoal: Int) {
        sharedPreferences.edit()
            .putString(goalPreferencesKey, newSavedGoal.toString())
            .apply()
    }

    fun saveTodayDateInPreferences() {
        sharedPreferences.edit().putInt(datePreferencesKey, today).apply()
    }

    fun isSavedDateToday(): Boolean {
        val savedDay = sharedPreferences.getInt(datePreferencesKey, 0)
        return today == savedDay
    }

    fun getKcalSum(): LiveData<Double> {
        return localDataSource.getKcalSum()
    }
}