package com.kan.salads

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_layout.view.*

class SaladListAdapter(var context: Context, var items: MutableList<ShoppingCartItemViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemSelected: (itemId:String) -> Unit = {
        println("In adapter")
    }
    var onItemDeSelected: (itemId:String) -> Unit = {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val viewHolder = (holder as ViewHolder)
        viewHolder.onItemDeSelected = onItemDeSelected
        viewHolder.onItemSelected = onItemSelected
        viewHolder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false)
        return ViewHolder(view)
    }

    fun setNewItems(newItems: List<ShoppingCartItemViewModel>){
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var itemViewModel: ShoppingCartItemViewModel? = null

        fun bind(item: ShoppingCartItemViewModel) {
            itemViewModel = item
            itemView.textView.text = item.salad.name
            if (item.selected) {
                setSelected()
            } else {
                setDeSelected()
            }
            itemView.setOnClickListener {
                onClicked(item)
            }
        }

        private fun onClicked(item: ShoppingCartItemViewModel) {
            if (item.selected) {
                onItemDeSelected(item.salad.uuid)
                setDeSelected()
            } else {
                onItemSelected(item.salad.uuid)
                setSelected()
            }
            item.selected = !item.selected
        }

        private fun setDeSelected() {
            itemView.textView.typeface = Typeface.DEFAULT
        }

        private fun setSelected() {
            itemView.textView.typeface = Typeface.DEFAULT_BOLD
        }

        var onItemSelected: (itemId: String) -> Unit = {println("In viewholder")}
        var onItemDeSelected: (itemId: String) -> Unit = {}
    }
}