package com.example.antigaspi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TodoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)

        // Get the data passed from MainActivity
        val todoTitle = intent.getStringExtra("todo_title")

        // Set the todo title to the TextView
        val tvTodoDetail = findViewById<TextView>(R.id.tvTodoDetail)
        tvTodoDetail.text = todoTitle
    }
}
