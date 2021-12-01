package com.syukron.mymealdiary.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.syukron.mymealdiary.data.model.FoodList
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val BASE_URL = "https://api.calorieninjas.com/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface CalorieNinjasApiService {
    @GET("nutrition?query")
    suspend fun getFoodList(@Header("X-Api-Key") header: String, @Query("query") query: String):
            FoodList
}

object RemoteDataSource {
    val httpClient: CalorieNinjasApiService by lazy {
        retrofit.create(CalorieNinjasApiService::class.java)
    }
}