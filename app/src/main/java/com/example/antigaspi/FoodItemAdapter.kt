package com.example.antigaspi

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent

class FoodItemAdapter(
    private val context: Context,
    private val foodItems: MutableList<FoodItem>
) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    inner class FoodItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvFoodItemTitle: TextView = itemView.findViewById(R.id.tvFoodItemTitle)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = foodItems[position]
                    val intent = Intent(context, FoodItemDetailActivity::class.java)
                    intent.putExtra("todo_title", selectedItem.title)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        return FoodItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.food_item,
                parent,
                false
            )
        )
    }

    fun addFoodItem(foodItem:FoodItem) {
        foodItems.add(foodItem)
        notifyItemInserted(foodItems.size - 1)
    }

    fun deleteDoneFoodItems() {
        foodItems.removeAll { item ->
            item.isChecked
        }

        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tvFoodItemTitle: TextView, isChecked: Boolean) {
        if (isChecked) {
            tvFoodItemTitle.paintFlags = tvFoodItemTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvFoodItemTitle.paintFlags = tvFoodItemTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val curFoodItem = foodItems[position]
        holder.itemView.apply {
            val tvFoodItemTitle = findViewById<TextView>(R.id.tvFoodItemTitle)
            val tvExpirationDate = findViewById<TextView>(R.id.tvExpirationDate)
            val cbDone = findViewById<CheckBox>(R.id.cbDone)
            tvFoodItemTitle.text = curFoodItem.title
            tvExpirationDate.text = curFoodItem.expirationDate.toString()
            cbDone.isChecked = curFoodItem.isChecked
            toggleStrikeThrough(tvFoodItemTitle, curFoodItem.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvFoodItemTitle, isChecked)
                curFoodItem.isChecked = !curFoodItem.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return foodItems.size
    }

    fun getFoodItems(): MutableList<FoodItem> {
        return foodItems
    }
}