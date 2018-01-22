package com.kan.salads

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class SaladListAdapter(private var context: Context, private var items: MutableList<ShoppingCartItemViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemSelected: (itemId:String) -> Unit = {
        println("In adapter")
    }
    var onItemDeSelected: (itemId:String) -> Unit = {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val viewHolder = (holder as SaladViewHolder)
        viewHolder.onItemDeSelected = onItemDeSelected
        viewHolder.onItemSelected = onItemSelected
        viewHolder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false)
        return SaladViewHolder(view)
    }

    fun setNewItems(newItems: List<ShoppingCartItemViewModel>){
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }
}