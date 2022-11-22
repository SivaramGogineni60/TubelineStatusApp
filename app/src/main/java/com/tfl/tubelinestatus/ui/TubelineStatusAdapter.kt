package com.tfl.tubelinestatus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.tfl.tubelinestatus.R

internal class TubelineStatusAdapter(private var itemsList: List<TubelineStatusUiModel>) :
    RecyclerView.Adapter<TubelineStatusAdapter.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemColorTextView: TextView = view.findViewById(R.id.tubelineColorCodeTextView)
        var itemNameTextView: TextView = view.findViewById(R.id.tubelineNameTextView)
        var statusTextView: TextView = view.findViewById(R.id.StatusTextView)
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tubeline_status, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.itemColorTextView.setBackgroundColor(item.color)
        holder.itemNameTextView.text = item.name
        holder.statusTextView.text = item.statusSeverityDescription
    }
    override fun getItemCount(): Int {
        return itemsList.size
    }
}