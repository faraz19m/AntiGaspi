package com.example.antigaspi

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Tests for the SettingsActivity.
 */
@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get : Rule
    var mActivityRule = ActivityScenarioRule(SettingsActivity::class.java)

 
    /**
     * This test clicks on a view with a FoodItem and checks if the data is correct on the FoodItemDetailActivity.
     */
    @Test
    fun checkChangingNotificationDelay() {
        // click on the first item
        onView(withId(R.id.sSetExpirationDays)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)))).atPosition(0).perform(click())
        // check if value is correctly saved
        val sh = SharedPreferencesHelper(InstrumentationRegistry.getInstrumentation().targetContext)
        assert(sh.getDaysBeforeExpiration() == 0)
    }


    /**
     * This test clicks on a view with a FoodItem and checks if the data is correct on the FoodItemDetailActivity.
     */
    @Test
    fun checkDarkMode() {
        // click night mode switch
        onView(withId(R.id.swDayNightMode)).perform(click())

        // get night mode value
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()

        assert(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES )

    }

    

    @After
    fun tearDown() {
        //clean up code
    }
}