package com.letrix.anime.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.ItemAnimeBinding
import com.letrix.anime.utils.ImageLoader

class AnimeAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Anime, AnimeAdapter.ItemHolder>(object : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class ItemHolder(private val binding: ItemAnimeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Anime) {
            binding.apply {
                title.text = item.title
                ImageLoader.loadImage(item.poster, poster)
                clickableLayout.setOnClickListener {
                    itemClickListener.onClick(item)
                }
            }
        }
    }

    interface ItemClickListener {
        fun onClick(item: Anime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}