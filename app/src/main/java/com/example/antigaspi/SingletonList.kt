package com.example.antigaspi

/**
 * Singleton class holding a list of [FoodItem].
 *
 * Get the data with [SingletonList.theInstance.list].
 *
 * @property list the data.
 * @property theInstance the single instance.
 * @property getInstance Use this to get the instance.
 */
class SingletonList {
    var list: ArrayList<FoodItem> = arrayListOf()
    companion object {
        val theInstance: SingletonList = SingletonList()
        fun getInstance(): SingletonList {
            return theInstance
        }
    }







}