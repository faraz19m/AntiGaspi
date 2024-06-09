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


/**
 * Represents a page showing detail about a [FoodItem].
 */
class FoodItemDetailActivity : AppCompatActivity() {


    companion object {
        /**
         * The key used to pass the index of the [FoodItem] that this activity represents.
         * The index is from the list inside [SingletonList].
         */
        const val PREF_ITEM_INDEX:String = "item_index"
    }


    private lateinit var ocrHelper: OCRHelper
    private lateinit var tvFoodItemTitle: TextView
    private lateinit var tvExpirationDate: TextView
    private lateinit var tvScannedText: TextView
    private lateinit var tvDeepFreeze: TextView
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)

        ocrHelper = OCRHelper(this)
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)


        // Get the index passed from MainActivity
        val index = intent.getIntExtra(PREF_ITEM_INDEX,0)

        // Set textViews
        // SingletonList is used to get/modify the list
        tvFoodItemTitle = findViewById(R.id.tvFoodItemTitle)
        tvFoodItemTitle.text = SingletonList.theInstance.list[index].title

        tvExpirationDate = findViewById(R.id.tvExpirationDate)
        tvExpirationDate.text = SingletonList.theInstance.list[index].getPrettyDate()

        tvDeepFreeze = findViewById(R.id.tvDeepFreeze)
        tvDeepFreeze.text = if (SingletonList.theInstance.list[index].isDeepFrozen)  "This item is frozen" else "This item is not frozen"

        tvDeepFreeze.setOnClickListener(
            {
                SingletonList.theInstance.list[index].isDeepFrozen = !SingletonList.theInstance.list[index].isDeepFrozen
                tvDeepFreeze.text = if (SingletonList.theInstance.list[index].isDeepFrozen)  "This item is frozen" else "This item is not frozen"
            }
        )


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
