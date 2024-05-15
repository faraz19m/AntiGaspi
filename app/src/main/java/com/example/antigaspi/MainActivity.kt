package com.example.antigaspi

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoAdapter = TodoAdapter(mutableListOf())

        val rvTodoItems = findViewById<RecyclerView>(R.id.rvTodoItems)
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        val btnAddTodo = findViewById<Button>(R.id.btnAddTodo)
        btnAddTodo.setOnClickListener {
            val etTodoTitle = findViewById<EditText>(R.id.etTodoTitle)
            val todoTitle = etTodoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                etTodoTitle.text.clear()
            }
        }

        val btnDeleteDone = findViewById<Button>(R.id.btnDeleteDoneTodos)
        btnDeleteDone.setOnClickListener {
            todoAdapter.deleteDoneTodos()
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

                Log.d("myapp", "sending request...")
                val response: HttpResponse = httpClient.get(url)

                val bd = response.body() as String


                try {
                    var json = JSONObject(bd)
                    var product = JSONObject(json.getString("product"))
                    Log.d("myapp", product.toString().take(20))

                    var productName = product.getString("abbreviated_product_name")
                    Log.d("myapp", (productName).toString())
                    todoAdapter.addTodo(Todo(productName.take(20)))
                } catch (e: Exception) {
                    // TODO: do something else if there is an error
                    todoAdapter.addTodo(Todo(e.toString()))
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
        super.onDestroy()
    }


}