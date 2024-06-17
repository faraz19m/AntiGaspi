package com.example.antigaspi

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class OCRHelperTest {

    private lateinit var context: Context
    private lateinit var ocrHelper: OCRHelper
    private lateinit var bitmap: Bitmap

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        ocrHelper = OCRHelper(context)
        bitmap = mock(Bitmap::class.java)
    }

    @Test
    // This test verifies that the OCRHelper correctly recognizes and formats text from an image
    fun recognizeTextFromImage_Success() {
        // Arrange
        val recognizedText = "Recognized text: Expiry Date: 12/12/2022"
        val expectedFormattedDate = "12.12.2022"
        val visionText = mock(Text::class.java)
        `when`(visionText.text).thenReturn(recognizedText)

        // Act
        var actualFormattedDate: String? = null
        ocrHelper.recognizeTextFromImage(bitmap) { date ->
            actualFormattedDate = date
        }

        // Assert
        assertEquals(expectedFormattedDate, actualFormattedDate)
    }

    @Test
    // This test checks the OCRHelper’s behavior when text recognition fails
    fun recognizeTextFromImage_Failure() {
        // Arrange
        val exception = Exception("Text recognition failed")

        // Act
        var callbackInvoked = false
        ocrHelper.recognizeTextFromImage(bitmap) { date ->
            callbackInvoked = true
            assertNull(date)
        }

        // Assert
        assertTrue(callbackInvoked)
    }
}
