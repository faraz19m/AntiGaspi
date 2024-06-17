package com.example.antigaspi

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*


//The tests use the Mockito framework for mocking dependencies,
// which allows you to simulate how the class interacts with its dependencies.

class ExpiryCheckerTest {

    private lateinit var context: Context
    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var expiryChecker: ExpiryChecker

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        foodItemAdapter = mock(FoodItemAdapter::class.java)
        sharedPreferencesHelper = mock(SharedPreferencesHelper::class.java)
        notificationHelper = mock(NotificationHelper::class.java)
        expiryChecker = ExpiryChecker(context, foodItemAdapter, sharedPreferencesHelper)
    }

    //This test verifies that a notification is sent when an item is about to expire
    @Test
    fun checkForExpiringItems_NotificationSent() {
        // Arrange
        val expirationDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2) // Set expiration date 2 days from now
        }.time

        // The when method is used to define the behavior of the mocked objects when certain methods are called on them.
        val foodItems = arrayListOf(FoodItem("Milk", expirationDate = expirationDate)) // Expires in 2 days
        `when`(foodItemAdapter.getFoodItems()).thenReturn(foodItems)
        `when`(sharedPreferencesHelper.getDaysBeforeExpiration()).thenReturn(1) // Notify 1 day before expiration

        // Act
        expiryChecker.checkForExpiringItems()

        // Assert
        // The verify method from Mockito is used to check that certain interactions took place during the test.
        verify(notificationHelper).sendNotification(
            "Food Expiration Alert",
            "Your food item Milk is expiring in 1 days."
        )
    }

    //This test checks that no notification is sent when an item is not close to expiring
    // This ensures that sendNotification is never called
    // because the item expires in 5 days, and the setting is to notify 3 days before expiration.
    @Test
    fun checkForExpiringItems_NoNotificationSent() {
        // Arrange
        val expirationDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 5) // Set expiration date 4 days from now
        }.time

        // The when method is used to define the behavior of the mocked objects when certain methods are called on them.
        val foodItems = arrayListOf(FoodItem("Milk", expirationDate = expirationDate)) // Expires in 5 days
        `when`(foodItemAdapter.getFoodItems()).thenReturn(foodItems)
        `when`(sharedPreferencesHelper.getDaysBeforeExpiration()).thenReturn(3) // Notify 3 days before expiration

        // Act
        expiryChecker.checkForExpiringItems()

        // Assert
        // The verify method from Mockito is used to check that certain interactions took place during the test.
        verify(notificationHelper, never()).sendNotification(anyString(), anyString())
    }
}