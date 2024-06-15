package com.example.antigaspi

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
    fun saveFoodItemList(foodItemList: MutableList<FoodItem>) {
        val jsonString = gson.toJson(foodItemList, object : TypeToken<MutableList<FoodItem?>?>(){}.type)
        sharedPreferences.edit().putString("food_list", jsonString).apply()
    }

    fun loadFoodItemList(): MutableList<FoodItem> {
        val jsonString = sharedPreferences.getString("food_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<FoodItem>>(){}.type

        var list:MutableList<FoodItem> = gson.fromJson(jsonString, type)

        return list
    }
    fun getDaysBeforeExpiration(): Int {
        return prefs.getInt("ExpirationDaysPosition", 3) // Default to 3 days
    }
}
