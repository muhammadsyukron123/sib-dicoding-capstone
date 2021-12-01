package com.syukron.mymealdiary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.syukron.mymealdiary.data.model.Food

@Database(entities = [Food::class], version = 24, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {
    abstract val foodDao: FoodDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null
        fun getInstance(context: Context): FoodDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FoodDatabase::class.java,
                        "saved_foods_table"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}