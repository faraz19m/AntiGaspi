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
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback

class FoodItemAdapter(
    private val context: Context,

) : ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(diffUtil),Filterable {
    private var foodItems: ArrayList<FoodItem> = arrayListOf()

    // Filter by a CharSequence.
    // Only keep the foodItems whose title contains the CharSequence.
    private val filter : Filter = object : Filter() {
        override fun performFiltering(input: CharSequence): FilterResults {
            val filteredList = if (input.isEmpty()) {
                foodItems
            } else {
                foodItems.filter { it.title.lowercase().contains(input) }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(input: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<FoodItem>)
        }
    }

    inner class FoodItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvFoodItemTitle: TextView = itemView.findViewById(R.id.tvFoodItemTitle)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)

        init {
            // Add listener to each item. This listener starts a new intent with FoodItemDetailActivity.
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = currentList[position]
                    val intent = Intent(context, FoodItemDetailActivity::class.java)
                    intent.putExtra("todo_title", selectedItem.title)
                    context.startActivity(intent)
                }
            }


        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FoodItem>() {
            override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem) =
                oldItem.compareContents(newItem)

            override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem) =
                oldItem == newItem
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

    // Remove all foodItems from the list that have [isChecked] == true
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
        val curFoodItem = currentList[position]

        holder.itemView.apply {
            val tvFoodItemTitle = findViewById<TextView>(R.id.tvFoodItemTitle)
            val tvExpirationDate = findViewById<TextView>(R.id.tvExpirationDate)
            val cbDone = findViewById<CheckBox>(R.id.cbDone)
            tvFoodItemTitle.text = curFoodItem.title
            tvExpirationDate.text = curFoodItem.getPrettyDate()
            cbDone.isChecked = curFoodItem.isChecked
            toggleStrikeThrough(tvFoodItemTitle, curFoodItem.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvFoodItemTitle, isChecked)
                curFoodItem.isChecked = !curFoodItem.isChecked
            }
        }
    }


    fun getFoodItems(): ArrayList<FoodItem> {
        return foodItems
    }


    override fun getFilter(): Filter {
        return filter
    }
}