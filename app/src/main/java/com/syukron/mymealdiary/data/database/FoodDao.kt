package com.syukron.mymealdiary.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.syukron.mymealdiary.data.model.Food

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foodList: List<Food>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food)

    @Update
    suspend fun updateFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("DELETE FROM saved_foods_table WHERE list_type != 4")
    suspend fun clearAllExceptHistoryFoods()

    @Query("DELETE FROM saved_foods_table WHERE list_type = 4")
    suspend fun clearOnlyHistoryFoods()

    @Query("SELECT * FROM saved_foods_table WHERE list_type != 4")
    suspend fun getAllExceptHistoryFoods(): List<Food>

    @Query("SELECT * FROM saved_foods_table WHERE list_type = :listType")
    fun getAllFoodsWithListType(listType: Int): LiveData<List<Food>>

    @Query("SELECT TOTAL(calories) FROM saved_foods_table WHERE list_type != 4")
    fun getKcalSum(): LiveData<Double>
}