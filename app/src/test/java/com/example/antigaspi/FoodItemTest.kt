package com.example.antigaspi

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Unit tests for [FoodItem].
 */
class FoodItemTest {
    /**
     * Test [FoodItem.compareContents] for two items that have the same fields.
     */
    @Test
    fun testCompareContents() {
        val d = Date()
        val f1 = FoodItem("test", false, d, false )
        val f2 = FoodItem("test", false, d, false )
        assert(f1.compareContents(f2))
    }

    /**
     * Test [FoodItem.compareDates].
     *
     * 3 cases:
     * - both items have the same date
     * - first item has a bigger date
     * - second item has a bigger date
     */
    @Test
    fun testCompareDates() {
        val d = Date()
        val f1 = FoodItem("test", false, d, false )
        val f2 = FoodItem("test", false, d, false )
        assertEquals(0,f1.compareDates(f2))

        val f3 = FoodItem("test", false, Date(5000), false )
        val f4 = FoodItem("test", false, Date(0), false )
        assert(f3.compareDates(f4)>0)


        val f5 = FoodItem("test", false, Date(0), false )
        val f6 = FoodItem("test", false, Date(5000), false )
        assert(f5.compareDates(f6)<0)
    }

}