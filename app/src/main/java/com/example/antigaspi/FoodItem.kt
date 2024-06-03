package com.example.antigaspi

import java.util.Date

data class FoodItem(
    var title:String,
    var isChecked: Boolean = false,
    var expirationDate: Date? = null,
    var deepFreeze: Boolean = false,

    )