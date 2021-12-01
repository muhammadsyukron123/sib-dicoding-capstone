package com.syukron.mymealdiary.util

import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.data.model.ListType

fun List<Food>.sum(): Food = takeIf { it.isNotEmpty() }
    ?.reduce { acc: Food, food: Food -> acc + food } ?: Food()

fun List<Food>.changeListType(listType: ListType) = map { food ->
    food.copy(listType = listType.ordinal)
}
