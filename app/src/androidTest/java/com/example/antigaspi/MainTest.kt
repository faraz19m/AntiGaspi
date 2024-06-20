package com.example.antigaspi

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Rule
import org.junit.After
import java.util.Date

/**
 * Tests for the MainActivity and the FoodItemDetailActivity.
 */
@RunWith(AndroidJUnit4::class)
class MainTest {

    @get : Rule
    var mActivityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Adds several FoodItem to the list.
     */
    @Before
    fun setUp() {
        val d1 = Date(1,1,1)
        val d2 = Date(2,2,2)
        val d3 = Date(3,3,3)
        val l = SingletonList.getInstance()
        l.list.add(FoodItem("Food1", false, d1, false))
        l.list.add(FoodItem("Food2", false, d2, true))
        l.list.add(FoodItem("Food3", true, d3, true))
    }

    /**
     * This test clicks on a view with a FoodItem and checks if the data is correct on the FoodItemDetailActivity.
     */
    @Test
    fun checkOpenDetailActivity() {

        InstrumentationRegistry.getInstrumentation().waitForIdle {
            onView(withText("Food1")).perform(click())
            // check if data is correct
            onView(withId(R.id.tvFoodItemTitle)).check(matches(withText("Food1")))
            onView(withId(R.id.btnDeepFreeze)).check(matches(withText("Freeze")))
            onView(withId(R.id.tvExpirationDate)).check(matches(withText(FoodItem("Food1", false, Date(1,1,1), false).getPrettyDate())))
        }
    }

    /**
     * Change the deep freeze status of an item, and check if it has changed.
     */
    @Test
    fun checkChangeDeepFreeze() {

        InstrumentationRegistry.getInstrumentation().waitForIdle {
            onView(withText("Food1")).perform(click())
            // click on freeze button
            onView(withId(R.id.btnDeepFreeze)).perform(click())

            // check freeze status
            onView(withId(R.id.btnDeepFreeze)).check(matches(withText("Unfreeze")))

        }
    }

    /**
     * Clicks on the delete button and checks if the list has been updated accordingly.
     */
    @Test
    fun checkRemoveSingleItem() {
        val lBefore = SingletonList.getInstance().list.size
        onView(withId(R.id.btnDeleteDoneItems)).perform(click())

        InstrumentationRegistry.getInstrumentation().waitForIdle {
            val lAfter = SingletonList.getInstance().list.size

            assert(lBefore + 1 == lAfter)
        }

    }

    /**
     * Checks if all the items in the list are in order.
     */
    @Test
    fun checkItemOrder() {
        InstrumentationRegistry.getInstrumentation().waitForIdle {
            val l = SingletonList.getInstance().list
            var previousItem = l[0]
            for (i in 1..<l.size) {
                val item = l[i]
                assert(item.compareDates(previousItem) <= 0)
                previousItem = item
            }

        }

    }

    @After
    fun tearDown() {
        SingletonList.getInstance().list = arrayListOf()
    }
}