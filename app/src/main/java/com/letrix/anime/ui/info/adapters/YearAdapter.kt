package com.letrix.anime.ui.info.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.databinding.ItemChipBinding

class YearAdapter(private val list: List<String>, private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<YearAdapter.ItemHolder>() {
    inner class ItemHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(year: String) {
            binding.text.text = year
            binding.clickableLayout.setOnClickListener {
                clickListener.onClick(year)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.onBind(list[position])

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onClick(year: String)
    }
}