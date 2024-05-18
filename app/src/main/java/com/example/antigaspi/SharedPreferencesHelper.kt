package com.example.antigaspi

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTodoList(todoList: List<Todo>) {
        val jsonString = gson.toJson(todoList)
        sharedPreferences.edit().putString("todo_list", jsonString).apply()
    }

    fun loadTodoList(): MutableList<Todo> {
        val jsonString = sharedPreferences.getString("todo_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Todo>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}
