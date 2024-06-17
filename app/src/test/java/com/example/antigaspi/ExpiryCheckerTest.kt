package com.example.antigaspi

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

// The tests use the Mockito framework for mocking dependencies,
// which allows you to simulate how the class interacts with its dependencies.

class ExpiryCheckerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var foodItemAdapter: FoodItemAdapter

    @Mock
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Mock
    private lateinit var notificationHelper: NotificationHelper

    @InjectMocks
    private lateinit var expiryChecker: ExpiryChecker

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun checkForExpiringItems_NotificationSent() {  //This test verifies that a notification is sent when an item is about to expire
        val expirationDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2) // Set expiration date 2 days from now
        }.time

        val foodItems = arrayListOf(FoodItem("Milk", expirationDate = expirationDate)) // Expires in 2 days
        `when`(foodItemAdapter.getFoodItems()).thenReturn(foodItems)
        `when`(sharedPreferencesHelper.getDaysBeforeExpiration()).thenReturn(1) // Notify 1 day before expiration

        expiryChecker.checkForExpiringItems()

        verify(notificationHelper).sendNotification(
            "Food Expiration Alert",
            "Your food item Milk is expiring in 1 days."
        )
    }

    @Test
    fun checkForExpiringItems_NoNotificationSent() { //This test checks that no notification is sent when an item is not close to expiring
        val expirationDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 5) // Set expiration date 5 days from now
        }.time

        val foodItems = arrayListOf(FoodItem("Milk", expirationDate = expirationDate)) // Expires in 5 days
        `when`(foodItemAdapter.getFoodItems()).thenReturn(foodItems)
        `when`(sharedPreferencesHelper.getDaysBeforeExpiration()).thenReturn(3) // Notify 3 days before expiration

        expiryChecker.checkForExpiringItems()

        verify(notificationHelper, never()).sendNotification(anyString(), anyString())
    }
}