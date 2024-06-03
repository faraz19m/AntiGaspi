package com.example.antigaspi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.SharedPreferences

class FoodItemDetailActivity : AppCompatActivity() {

    private lateinit var ocrHelper: OCRHelper
    private lateinit var tvFoodItemTitle: TextView
    private lateinit var tvScannedText: TextView
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)

        ocrHelper = OCRHelper(this)
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

        // Get the data passed from MainActivity
        val foodItemTitle = intent.getStringExtra("todo_title")

        // Set the title to the TextView
        tvFoodItemTitle = findViewById(R.id.tvFoodItemTitle)
        tvFoodItemTitle.text = foodItemTitle

        // Initialize the scanned text TextView
        tvScannedText = findViewById(R.id.tvScannedText)
        tvScannedText.text = ""

        // Load previously saved scanned text
        val savedText = sharedPreferences.getString("scannedText", "")
        if (savedText != null && savedText.isNotEmpty()) {
            tvScannedText.text = savedText
        }

        val btnScanText = findViewById<Button>(R.id.btnScanText)
        btnScanText.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            ocrHelper.recognizeTextFromImage(imageBitmap) { recognizedText ->
                runOnUiThread {
                    tvScannedText.text = recognizedText
                    Toast.makeText(this, "Text recognized: $recognizedText", Toast.LENGTH_LONG).show()
                    val editor = sharedPreferences.edit()
                    editor.putString("scannedText", recognizedText)
                    editor.apply()
                }
            }
        }
    }

    override fun onDestroy() {
        // Clear the SharedPreferences when leaving the activity
        super.onDestroy()
    }
}
