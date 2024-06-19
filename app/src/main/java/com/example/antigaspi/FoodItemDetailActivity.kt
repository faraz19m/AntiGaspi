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
import android.content.DialogInterface
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date


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
    private lateinit var btnDeepFreeze: Button
    private lateinit var btnSelectDate: Button
    private lateinit var btnEditTitle: ImageButton
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)

        ocrHelper = OCRHelper(this)
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

        val sharedPreferencesHelper = SharedPreferencesHelper(this)


        // Get the index passed from MainActivity
        val index = intent.getIntExtra(PREF_ITEM_INDEX,0)

        // Set textViews
        // SingletonList is used to get/modify the list
        tvFoodItemTitle = findViewById(R.id.tvFoodItemTitle)
        tvFoodItemTitle.text = SingletonList.theInstance.list[index].title

        tvExpirationDate = findViewById(R.id.tvExpirationDate)
        tvExpirationDate.text = SingletonList.theInstance.list[index].getPrettyDate()

        btnDeepFreeze = findViewById(R.id.btnDeepFreeze)
        btnDeepFreeze.text = if (SingletonList.theInstance.list[index].isDeepFrozen)  "Unfreeze" else "Freeze"

        btnDeepFreeze.setOnClickListener {
            SingletonList.theInstance.list[index].isDeepFrozen = !SingletonList.theInstance.list[index].isDeepFrozen
            btnDeepFreeze.text = if (SingletonList.theInstance.list[index].isDeepFrozen) "Unfreeze" else "Freeze"
            sharedPreferencesHelper.saveFoodItemList(SingletonList.theInstance.list)

        }
        // set listener for selecting expiration date
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectDate.setOnClickListener {
            val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select expiration date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setPositiveButtonText("Submit")
                .build()
            materialDatePicker.addOnPositiveButtonClickListener {time->
                SingletonList.theInstance.list[index].expirationDate = Date(time)
                tvExpirationDate.text = SingletonList.theInstance.list[index].getPrettyDate()
                sharedPreferencesHelper.saveFoodItemList(SingletonList.theInstance.list)
            }
            materialDatePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            materialDatePicker.show(supportFragmentManager, "Date picker")
        }

        // set listener for changing the title
        btnEditTitle = findViewById(R.id.btnEditTitle)
        btnEditTitle.setOnClickListener {
            // edittext in dialog
            val et = EditText(this)
            // create dialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Edit title")
                .setView(et)
            builder.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                val data = et.text.toString()
                if (data != "") {
                    SingletonList.theInstance.list[index].title = data
                    sharedPreferencesHelper.saveFoodItemList(SingletonList.theInstance.list)
                }
                et.text.clear()
            }
            builder.setNegativeButton("Back") { _: DialogInterface, _: Int ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }




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
