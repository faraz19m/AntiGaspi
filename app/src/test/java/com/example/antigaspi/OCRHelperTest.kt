package com.example.antigaspi

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class OCRHelperTest {

    @Mock
    private lateinit var context: Context

    @InjectMocks
    private lateinit var ocrHelper: OCRHelper

    @Mock
    private lateinit var bitmap: Bitmap

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun recognizeTextFromImage_Success() {
        val recognizedText = "Recognized text: Expiry Date: 12/12/2022"
        val expectedFormattedDate = "12.12.2022"
        val visionText = mock(Text::class.java)
        `when`(visionText.text).thenReturn(recognizedText)

        var actualFormattedDate: String? = null
        ocrHelper.recognizeTextFromImage(bitmap) { date ->
            actualFormattedDate = date
        }

        assertEquals(expectedFormattedDate, actualFormattedDate)
    }

    @Test
    fun recognizeTextFromImage_Failure() {
        val exception = Exception("Text recognition failed")

        var callbackInvoked = false
        ocrHelper.recognizeTextFromImage(bitmap) { date ->
            callbackInvoked = true
            assertNull(date)
        }

        assertTrue(callbackInvoked)
    }
}