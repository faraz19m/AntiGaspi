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

/**
 * Adapter that has a filter.
 * Set [currentFilter] and [showOnlyDeepFreeze] first and then use [filter] to apply that filter.
 */
class FoodItemAdapter(
    private val context: Context,

    ) : ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(diffUtil), Filterable {
    /**
     * List of foodItems. The current displayed list can be accessed by currentList
     */
    private var foodItems: ArrayList<FoodItem> = arrayListOf()

    /**
     * String that is used to filter ie only display certain items.
     */
    var currentFilter: String = ""

    /**
     * If true then the recyclerView only shows items where [FoodItem.isChecked].
     */
    var showOnlyDeepFreeze = false

    /**
     * Filters by a CharSequence.
     * Only keep the foodItems whose title contains the CharSequence.
     */
    private val filter: Filter = object : Filter() {
        override fun performFiltering(input: CharSequence): FilterResults {
            val filteredList = if (currentFilter.isEmpty()) {
                foodItems.filter { !showOnlyDeepFreeze || it.deepFreeze }
            } else {
                foodItems.filter { it.title.lowercase().contains(currentFilter) && (!showOnlyDeepFreeze || it.deepFreeze) }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(input: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<FoodItem>)
            notifyDataSetChanged()
        }
    }

    inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodItemTitle: TextView = itemView.findViewById(R.id.tvFoodItemTitle)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)

        init {
            // Add listener to each item. This listener starts a new intent with FoodItemDetailActivity.
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
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

    /**
     * Filters the items by [currentFilter] and [showOnlyDeepFreeze].
     * Also updates the recyclerView.
     * This should be used instead of [getFilter].
     */
    fun filter() {
        filter.filter("")
        notifyDataSetChanged()
    }

    fun add(foodItem: FoodItem) {
        foodItems.add(foodItem)

        //foodItems.binarySearch {  }
        sort()
        notifyDataSetChanged()

        //notifyItemInserted(foodItems.size - 1)
    }


    /**
     * Add all items from [list] to the list of foodItems of this adapter.
     */
    fun addAll(list: MutableList<FoodItem>) {

        foodItems.addAll(list)
        sort()
        notifyDataSetChanged()
    }

    /**
     * Get the list of food items.
     * For adding items it is better to use [add] or [addAll] than to directly use the underlying array.
     */
    fun getFoodItems(): ArrayList<FoodItem> {
        return foodItems
    }

    /**
     * Remove all foodItems from the list that have [FoodItem.isChecked] == true.
     * Updates the recyclerView.
     */
    fun deleteDoneFoodItems() {
        foodItems.removeAll { item ->
            item.isChecked
        }
        sort()
        filter()
        notifyDataSetChanged()
    }

    private fun sort() {
        // TODO: Sort by date instead
        foodItems.sortByDescending { it.title.length}


    }

    /**
     * Adds strike-through to the text of the [tvFoodItemTitle].
     */
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
                currentList[holder.bindingAdapterPosition].isChecked = isChecked
            }
        }
    }




    /**
     * @return the filter. Use the [filter] method instead of the getting this object.
     */
    override fun getFilter(): Filter {
        return filter
    }

}