package com.example.antigaspi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        val sw = findViewById<SwitchMaterial>(R.id.swDayNightMode)

        // set switch to checked if night mode is on
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        sw.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        // TODO: Use SharedPreferencesHelper for this? or similar class
        val sharedPreferences = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        // add listener
        sw.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putInt("NightMode", AppCompatDelegate.MODE_NIGHT_YES).apply()

            } else {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO).apply()
            }
            recreate()
        }
        // set back button on menu
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // set title on menu
        supportActionBar?.title = "Settings"

        // set up spinner
        val spinner: Spinner = findViewById(R.id.sSetExpirationDays)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.days_before_expiration,
            R.layout.spinner_settings
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
        val savedPosition = sharedPreferences.getInt("ExpirationDaysPosition", 0)
        spinner.setSelection(savedPosition, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menubar_settings, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // go back to main activity
            android.R.id.home -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val sharedPreferences = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("ExpirationDaysPosition", position)
            apply()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}