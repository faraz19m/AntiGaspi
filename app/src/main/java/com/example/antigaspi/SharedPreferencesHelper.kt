package com.example.antigaspi

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.recyclerview.widget.SortedList

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveFoodItemList(foodItemList: SortedList<FoodItem>) {
        // convert to mutableList first
        var ml = mutableListOf<FoodItem>()
        var i = 0
        while (i< foodItemList.size()) {
            ml.add(foodItemList.get(i))
            i++

        }
        val jsonString = gson.toJson(ml)
        sharedPreferences.edit().putString("todo_list", jsonString).apply()
    }

    fun loadFoodItemList(): MutableList<FoodItem> {
        val jsonString = sharedPreferences.getString("todo_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<FoodItem>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}
