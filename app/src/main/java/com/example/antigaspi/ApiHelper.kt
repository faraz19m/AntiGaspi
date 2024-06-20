package com.example.antigaspi

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.json.JSONObject

/**
 * This class is used to retrieve the food information from a barcode.
 */
class ApiHelper {

    private val httpClient = HttpClient(CIO) {}

    /**
     * URL used to access this api.
     */
    private val URL = "https://world.openfoodfacts.net/api/v2/product/"

    /**
     * Gets the name of a food item from a barcode.
     *
     * @return the name of the food with the [barcode], an empty string if an exception occurred.
     */
    suspend fun getFoodNameFromBarcode(barcode: String): String  {
            val url =  URL + barcode

            val response: HttpResponse = httpClient.get(url)

            val bd = response.body() as String

            try {
                val json = JSONObject(bd)
                val product = JSONObject(json.getString("product"))


                val productName = if (product.has("product_name")) {
                    product.getString("product_name")
                } else {
                    return ""
                }

                return productName.take(20)

            } catch (e: Exception) {
                return ""

        }


    }
}