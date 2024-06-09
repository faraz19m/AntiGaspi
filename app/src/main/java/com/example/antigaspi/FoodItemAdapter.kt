package com.example.antigaspi

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * Adapter that has a filter. Updates the recyclerview on adding/removing items.
 *
 * Use [currentFilter] and [showOnlyDeepFreeze] to change the filter.
 * Use [add], [addAll] and [deleteDoneFoodItems] to add or remove items. These methods automatically update the recyclerview.
 *
 * @property foodItems List of [FoodItem].
 * @property currentFilter Only items containing this in their title are displayed.
 * @property showOnlyDeepFreeze If true, only show items where [FoodItem.isDeepFrozen].
 */
class FoodItemAdapter: ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(diffUtil), Filterable {
    /**
     * List of foodItems.
     *
     * The current displayed list can be accessed by currentList.
     */
    private var foodItems: ArrayList<FoodItem> = arrayListOf()

    /**
     * String that is used to only display certain items.
     * Setting this value also calls [filter].
     */
    var currentFilter: String = ""
        set(value) {
            field = value
            filter()
        }

    /**
     * If the recyclerView only shows items where [FoodItem.isChecked].
     * Setting this value also calls [filter].
     */
    var showOnlyDeepFreeze = false
        set(value) {
            field = value
            filter()
        }

    /**
     * Filters by a CharSequence.
     * Only keep the foodItems whose title contains the CharSequence.
     */
    private val filter: Filter = object : Filter() {
        override fun performFiltering(input: CharSequence): FilterResults {
            val filteredList = if (currentFilter.isEmpty()) {
                foodItems.filter { !showOnlyDeepFreeze || it.isDeepFrozen }
            } else {
                foodItems.filter {
                    it.title.lowercase()
                        .contains(currentFilter) && (!showOnlyDeepFreeze || it.isDeepFrozen)
                }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(input: CharSequence, results: FilterResults?) {
            val res: ArrayList<*>? = results?.values as ArrayList<*>?
            submitList(res?.filterIsInstance<FoodItem>())
            notifyDataSetChanged()
        }
    }


    /**
     * Filters the displayed items by [currentFilter] and [showOnlyDeepFreeze].
     * Also updates the recyclerView.
     */
    private fun filter() {
        filter.filter("")
        notifyDataSetChanged()
    }

    /**
     * Adds [foodItem] to the list of items.
     * Also updates the recyclerview.
     */
    fun add(foodItem: FoodItem) {
        foodItems.add(foodItem)
        sort()
        filter()
        // TODO: Use notifyItemInserted() instead to make this more efficient. Also do this for addAll() and deleteDoneFoodItems().
        notifyDataSetChanged()
        //notifyItemInserted(foodItems.size - 1)
    }


    /**
     * Add all items from [list] to the list of foodItems of this adapter.
     * Also updates the recyclerview.
     */
    fun addAll(list: MutableList<FoodItem>) {
        foodItems.addAll(list)
        sort()
        filter()
        notifyDataSetChanged()
    }

    /**
     * Get the list of food items.
     * For adding items use [add] or [addAll] rather directly using this underlying array.
     */
    fun getFoodItems(): ArrayList<FoodItem> {
        return foodItems
    }

    /**
     * Remove all foodItems from the list that have [FoodItem.isChecked].
     * Also updates the recyclerview.
     */
    fun deleteDoneFoodItems() {
        foodItems.removeAll { item ->
            item.isChecked
        }
        sort()
        filter()
        notifyDataSetChanged()
    }

    /**
     * Sorts [foodItems].
     */
    private fun sort() {
        // TODO: Sort by date instead
        foodItems.sortByDescending { it.title.length }

    }

    /**
     * Toggles the strike-through of the text of the [tvFoodItemTitle].
     */
    private fun toggleStrikeThrough(tvFoodItemTitle: TextView, isChecked: Boolean) {
        if (isChecked) {
            tvFoodItemTitle.paintFlags = tvFoodItemTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvFoodItemTitle.paintFlags = tvFoodItemTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }


    /**
     * @return the filter.
     * For filtering, use the [filter] method instead of getting this object.
     */
    override fun getFilter(): Filter {
        return filter
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
            // Add listener to each item. This listener starts a new intent with FoodItemDetailActivity.
            this.setOnClickListener {
                    // get index first
                    val index = SingletonList.getInstance().list.indexOf(curFoodItem)

                    val intent = Intent(context, FoodItemDetailActivity::class.java)
                    intent.putExtra(FoodItemDetailActivity.PREF_ITEM_INDEX, index)
                    context.startActivity(intent)

            }
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        filter()

    }

    inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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


}