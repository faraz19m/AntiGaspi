package com.example.antigaspi

import android.content.Context
import android.util.Log
import java.util.Calendar
import kotlin.math.ceil

class ExpiryChecker(private val context: Context, private val foodItemAdapter: FoodItemAdapter, private val sharedPreferencesHelper: SharedPreferencesHelper) {

    private val notificationHelper = NotificationHelper(context)

    fun checkForExpiringItems() {
        val foodItems = foodItemAdapter.getFoodItems()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        // Get the threshold value from SharedPreferences
        var daysBeforeExpiration = sharedPreferencesHelper.getDaysBeforeExpiration()
        daysBeforeExpiration += 1

        for ((index,foodItem) in foodItems.withIndex()) {
            foodItem.expirationDate?.let { expiryDate ->
                // Calculate the notification date, which is expiration date minus daysBeforeExpiration
                calendar.time = expiryDate
                calendar.add(Calendar.DAY_OF_YEAR, -daysBeforeExpiration)
                val notificationDate = calendar.time

                val expdate = foodItem.expirationDate
                Log.d("ExpiryChecker function", "daysBeforeExpiration: $daysBeforeExpiration")
                Log.d("ExpiryChecker function", "expirationDate: $expdate")
                Log.d("ExpiryChecker function", "notification date: $notificationDate")
                Log.d("ExpiryChecker function", "currenDate: $currentDate")

                val diffInMillies = expiryDate.time - currentDate.time
                val diffInDays = ceil(diffInMillies.toDouble() / (1000 * 60 * 60 * 24)).toInt()

                Log.d("ExpiryChecker function", "comparison: $diffInDays")

                if (diffInDays in 0..daysBeforeExpiration) {
                    Log.d("ExpiryChecker function", "diffInMillies: $diffInMillies")
                    Log.d("ExpiryChecker function", "diffInDays: $diffInDays")
                    notificationHelper.sendNotification(
                        "Food Expiration Alert",
                        "Your food item ${foodItem.title} is expiring in $diffInDays days.",
                        index
                    )
                }
            }
        }
    }
}
