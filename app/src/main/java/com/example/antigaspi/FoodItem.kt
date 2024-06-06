package com.example.antigaspi

import java.util.Date

/**
 * A food item.
 * @param title The  name of the item
 * @param isChecked If the item is checked
 * @param expirationDate When the item expires
 * @param deepFreeze If the item is deep frozen.
 */
class FoodItem(
    var title:String,
    var isChecked: Boolean = false,
    var expirationDate: Date? = Date(),
    var deepFreeze: Boolean = false,

    ) {
    /**
     * @return A string of the [expirationDate] used to display to the user.
     */
    fun getPrettyDate(): String {
        val d = expirationDate ?: return "/"
        var res = ""
        res = res + d.date + "."+ d.month + "."+ (d.year + 1900)
        return res

    }

    /**
     * Compares the expirationDate between this and [item].
     * @return - A positive integer if this has a bigger date or [item] is null
     * - A negative integer if this has a smaller date or if this is null
     * - 0 if both are the same or both are null.
     */
    fun compareDates(item: FoodItem): Int {
        val t1 = this.expirationDate
        val t2 = item.expirationDate
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

    /**
     * @return True if all properties between [item] and this are the same.
     */
    fun compareContents(item:FoodItem): Boolean {
        return title == item.title && isChecked == item.isChecked && compareDates(item) == 0 && deepFreeze == item.deepFreeze
    }
}

