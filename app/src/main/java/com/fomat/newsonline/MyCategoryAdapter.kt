package com.fomat.newsonline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyCategoryAdapter(private val categoryList : ArrayList<String>) :
    RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item,
            parent, false)
        return MyCategoryAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = categoryList[position]
        holder.categoryName.text = currentItem
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val categoryName : TextView = itemView.findViewById(R.id.categoryName)
    }
}