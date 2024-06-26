package com.example.antigaspi

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern
import java.text.SimpleDateFormat
import java.util.Locale

class OCRHelper(private val context: Context) {

    fun recognizeTextFromImage(bitmap: Bitmap, callback: (String?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                Log.d("OCRHelper", "Recognized text: $recognizedText")
                val expiryDate = extractExpiryDate(recognizedText)
                Log.d("OCRHelper", "expiry date text: $expiryDate")
                val formattedDate = expiryDate?.let { formatDate(it) }
                Log.d("OCRHelper", "formatted date: $formattedDate")
                callback(formattedDate)
            }
            .addOnFailureListener { e ->
                Log.e("OCRHelper", "Text recognition failed: ${e.message}")
                callback(null)
            }
    }

    private fun extractExpiryDate(text: String): String? {
        // Regular expressions to match different date formats
        val pattern1 = Pattern.compile("(?m)\\b(\\d{2})[-/ ]?\\d{2}[-/ ]?(\\d{2})\\b") // 6 digit pattern
        val pattern2 = Pattern.compile("(?m)\\b(\\d{2})[-/ ]?\\d{2}[-/ ]?(\\d{4})\\b") // 8 digit pattern
        val pattern3 = Pattern.compile("(?m)\\b(\\d{2})[^\\d]*(?:\\p{L}{3})[^\\d]*(\\d{2})\\b") // dd-???-yy
        val pattern4 = Pattern.compile("(?m)\\b(\\d{2})[^\\d]*(?:\\p{L}{3})[^\\d]*(\\d{4})\\b") // dd-???-yyyy

        // Combine the patterns into a single array
        val patterns = arrayOf(pattern1, pattern2, pattern3, pattern4)

        var lastMatch: String? = null

        // Iterate over each line and perform pattern matching
        for (line in text.split("\n")) {
            // Iterate over each pattern
            for (pattern in patterns) {
                val matcher = pattern.matcher(line)
                // Find the last match for each pattern
                while (matcher.find()) {
                    lastMatch = matcher.group()
                }
            }
        }

        return lastMatch
    }

    private fun formatDate(dateStr: String): String {
        val inputFormats = arrayOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
            SimpleDateFormat("dd MM yyyy", Locale.getDefault()),
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
            SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
            SimpleDateFormat("dd MM yy", Locale.getDefault()),
            SimpleDateFormat("dd MMM yy", Locale.getDefault())
        )

        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        for (inputFormat in inputFormats) {
            try {
                val date = inputFormat.parse(dateStr)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                // Ignore parsing exceptions and try the next format
            }
        }

        // Return the original string if no formats matched
        return dateStr
    }


}
