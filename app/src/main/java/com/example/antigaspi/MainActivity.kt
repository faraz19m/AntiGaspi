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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var foodItemAdapter: FoodItemAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper


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


        // TODO: Remove dummy items
        foodItemAdapter.add(FoodItem(title = "ab", expirationDate = Date(124,7,20,1,1,1)))
        foodItemAdapter.add(FoodItem(title = "ac", expirationDate = Date(124,7,22,1,1,1)))
        foodItemAdapter.add(FoodItem(title = "ad", expirationDate = Date(124,7,24,1,1,1)))
        foodItemAdapter.add(FoodItem(title = "ae", expirationDate = Date(124,7,26,1,1,1)))

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
        builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
            processDataFromDialog(et.text.toString())
            et.text.clear()
        }
        builder.setNegativeButton("Back", { dialogInterface: DialogInterface, i: Int ->

        })

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

        // http client
        val httpClient = HttpClient(CIO) {

        }

        // send request to open food facts api
        fun sendRequest(barcode: String) = runBlocking {
            launch {
                val url = "https://world.openfoodfacts.net/api/v2/product/" + barcode

                Log.d("myapp Barcode", "Product JSON: ${barcode.toString()}")

                Log.d("myapp", "sending request...")
                val response: HttpResponse = httpClient.get(url)

                val bd = response.body() as String


                try {
                    val json = JSONObject(bd)
                    val product = JSONObject(json.getString("product"))
                    Log.d("myapp", "Product JSON: ${product.toString()}")

                    val productName = if (product.has("product_name")) {
                        product.getString("product_name")
                    } else {
                        "Unknown Product"
                    }
                    Log.d("myapp", productName)
                    foodItemAdapter.add(FoodItem(productName.take(20)))
                    sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
                } catch (e: Exception) {
                    Log.e("myapp", "Error parsing JSON: ${e.message}")
                    foodItemAdapter.add(FoodItem("Error: ${e.message}"))
                    sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
                }

            }
        }


        // button for scanning
        val btnScan = findViewById<Button>(R.id.btnScan)

        val scanner = GmsBarcodeScanning.getClient(this)


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
                            sendRequest(barcode.rawValue.toString())


                        }
                            .addOnCanceledListener {
                                Toast.makeText(applicationContext, "Scan cancelled", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // open settings page
            R.id.menubar_settings -> startActivity(Intent(this, SettingsActivity::class.java))

        }

        return super.onOptionsItemSelected(item)
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
            .addOnFailureListener { e ->
                // Handle failure...

            }
    }


    override fun onDestroy() {
        sharedPreferencesHelper.saveFoodItemList(foodItemAdapter.getFoodItems())
        super.onDestroy()
    }


}