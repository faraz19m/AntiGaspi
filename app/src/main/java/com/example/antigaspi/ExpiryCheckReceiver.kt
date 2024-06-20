package com.example.antigaspi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ExpiryCheckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ExpiryCheckReceiver", "Alarm triggered")

        // Ensure proper initialization
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val foodItemAdapter = FoodItemAdapter().apply {
            addAll(sharedPreferencesHelper.loadFoodItemList())
        }
        val expiryChecker = ExpiryChecker(context, foodItemAdapter, sharedPreferencesHelper)

        // Perform the check
        expiryChecker.checkForExpiringItems()
    }
}
