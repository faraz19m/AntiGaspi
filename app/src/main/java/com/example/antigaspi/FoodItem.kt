package com.example.antigaspi

import java.util.Date

class FoodItem(
    var title:String,
    var isChecked: Boolean = false,
    var expirationDate: Date? = Date(),
    var deepFreeze: Boolean = false,

    ) {
    fun getPrettyDate(): String {
        val d = expirationDate ?: return "/"
        var res = ""
        res = res + d.date + "."+ d.month + "."+ (d.year + 1900)
        return res

    }
    // Compare the expirationDate between this and foodItem.
    // Returns a positive integer if this has a bigger date or foodItem is null.
    // Returns a negative integer if this has a smaller date or if this is null.
    // Returns 0 if both are the same or null.
    fun compareDates(foodItem: FoodItem): Int {
        val t1 = this.expirationDate
        val t2 = foodItem.expirationDate
        return if (t1 == null && t2 == null) {
            0
        } else (if (t1  == null) {
            -1
        } else if (t2 == null) {
            1
        } else {
            t1.time - t2.time
        }).toInt()

    }

    fun compareContents(foodItem:FoodItem): Boolean {
        return title == foodItem.title && isChecked == foodItem.isChecked && compareDates(foodItem) == 0 && deepFreeze == foodItem.deepFreeze
    }
}

