package com.example.antigaspi

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.OptionalModuleApi
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var expiryChecker: ExpiryChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get night mode value from preferences
        val sharedPreferences = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(nightMode)



        setContentView(R.layout.activity_main)

        val singletonList: SingletonList = SingletonList.getInstance()



        // show logo on action bar
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_default_icon)
        supportActionBar?.title = " AntiGaspi"



        // For memory retention of the list
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        foodItemAdapter = FoodItemAdapter()
        singletonList.list = foodItemAdapter.getFoodItems()

        // add items from shared preferences
        foodItemAdapter.addAll(sharedPreferencesHelper.loadFoodItemList())

        // Set up recyclerview
        val rvFoodItems = findViewById<RecyclerView>(R.id.rvFoodItems)
        rvFoodItems.adapter = foodItemAdapter
        rvFoodItems.layoutManager = LinearLayoutManager(this)

        // editText for filtering by a string
        val etFilter = findViewById<EditText>(R.id.etFilter)
        etFilter.addTextChangedListener { input ->
            foodItemAdapter.currentFilter = input.toString()
        }


        // edittext in dialog
        val et = EditText(this)
        // create dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter food name")
            .setView(et)
        builder.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
            processDataFromDialog(et.text.toString())
            et.text.clear()
        }
        builder.setNegativeButton("Back") { _: DialogInterface, _: Int ->

        }

        // button to add food manually opens dialog
        val btnAddFoodManually = findViewById<Button>(R.id.btnAddItemManually)
        val dialog: AlertDialog = builder.create()
        btnAddFoodManually.setOnClickListener {
            dialog.show()



        }

        val cbDeepFreeze = findViewById<CheckBox>(R.id.cbSelectDeepFreeze)
        cbDeepFreeze.setOnCheckedChangeListener { _, isChecked ->
            foodItemAdapter.showOnlyDeepFreeze = isChecked
        }


        // button to delete items
        val btnDeleteDone = findViewById<Button>(R.id.btnDeleteDoneItems)
        btnDeleteDone.setOnClickListener {
            foodItemAdapter.deleteDoneFoodItems()
            sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
        }


        // setup scan options
        val scanOptions = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .enableAutoZoom()
            .build()

        val moduleInstallClient = ModuleInstall.getClient(this)



        // Initialize ExpiryChecker
        expiryChecker = ExpiryChecker(this, foodItemAdapter, sharedPreferencesHelper)

        // Check for expiring items
        expiryChecker.checkForExpiringItems()

        // button for scanning
        val btnScan = findViewById<Button>(R.id.btnScan)

        val scanner = GmsBarcodeScanning.getClient(this)

        val apiH = ApiHelper()

        // set listener for the scan
        btnScan.setOnClickListener {
            // check if barcode module is available
            moduleInstallClient
                .areModulesAvailable(scanner)
                .addOnSuccessListener {
                    if (it.areModulesAvailable()) {
                        // module is present, continue with scanning
                        scanner.startScan().addOnSuccessListener { barcode ->
                            Toast.makeText(applicationContext, "Scan success", Toast.LENGTH_LONG).show()

                            runBlocking {
                                launch {
                                    val productName = apiH.getFoodNameFromBarcode(barcode.rawValue.toString())
                                    if (productName.isNotEmpty()) {
                                        foodItemAdapter.add(FoodItem(productName, false, Date(), false))
                                        sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
                                    } else {
                                        Toast.makeText(applicationContext, "Error when retrieving item name ", Toast.LENGTH_LONG).show()
                                    }

                                }
                            }


                        }
                            .addOnCanceledListener {
                                Toast.makeText(applicationContext, "Scan cancelled", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(applicationContext, "Scan fail", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        // module is not present
                        moduleInstall(moduleInstallClient)
                        Toast.makeText(applicationContext, "Installing module first...", Toast.LENGTH_LONG).show()
                        Log.d("myapp", "module not installed...installing now")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Module checking failure...", Toast.LENGTH_LONG).show()
                    Log.d("myapp", "module install failure")
                }


        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // open settings page
            R.id.menubar_settings -> startActivity(Intent(this, SettingsActivity::class.java))

        }

        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        foodItemAdapter.update()
    }

    override fun onDestroy() {
        sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menubar, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    // Process the data from the dialog for adding items manually.
    // If data is not empty, then that value will be used to create a new food item and store it.
    private fun processDataFromDialog(data: String) {
        if (data != "") {
            foodItemAdapter.add(FoodItem( data, false, Date(),false))
            sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
        }

    }

    private fun moduleInstall(client: ModuleInstallClient) {

        val optionalModuleApi: OptionalModuleApi = GmsBarcodeScanning.getClient(this)
        val moduleInstallRequest =
            ModuleInstallRequest.newBuilder()
                .addApi(optionalModuleApi)
                .build()

        client.installModules(moduleInstallRequest)
            .addOnSuccessListener { response ->
                if (response.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.

                }
            }
            .addOnFailureListener {
                // Handle failure...

            }
    }


}