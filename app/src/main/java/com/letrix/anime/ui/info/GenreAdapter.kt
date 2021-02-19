package com.letrix.anime.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.data.Genre
import com.letrix.anime.databinding.ItemChipBinding

class GenreAdapter(private val list: List<Genre>, private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<GenreAdapter.ItemHolder>() {
    inner class ItemHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(genre: Genre) {
            binding.text.text = genre.title
            binding.clickableLayout.setOnClickListener {
                clickListener.onClick(genre)
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
        fun onClick(genre: Genre)
    }
}