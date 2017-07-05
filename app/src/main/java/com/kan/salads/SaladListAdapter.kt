package com.kan.salads

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.kan.salads.model.Salad
import com.kan.salads.model.ShoppingCart
import kotlinx.android.synthetic.main.list_layout.view.*

class SaladListAdapter(var context: Context, var lists: MutableList<Salad>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).bind(lists[position])
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selected = false
        val shoppingCart = ShoppingCart()

        fun bind(item: Salad) {
            itemView.textView.text = item.name
            itemView.setOnClickListener {
                selected = !selected
                if (selected) {
                    shoppingCart.addItem(FirebaseAuth.getInstance().currentUser!!.uid, item.uuid)
                    itemView.textView.typeface = Typeface.DEFAULT_BOLD
                } else {
                    shoppingCart.removeItem(FirebaseAuth.getInstance().currentUser!!.uid, item.uuid)
                    itemView.textView.typeface = Typeface.DEFAULT
                }
            }
        }
    }
}