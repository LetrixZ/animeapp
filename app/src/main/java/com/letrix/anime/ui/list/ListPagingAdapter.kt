package com.letrix.anime.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.ItemAnimeBinding
import com.letrix.anime.utils.ImageLoader


class ListPagingAdapter(private val listener: ItemClickListener) :
    PagingDataAdapter<Anime, ListPagingAdapter.ItemHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ItemHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClick(item)
                    }
                }
            }
        }

        fun bind(item: Anime) {
            binding.apply {
                ImageLoader.loadImage(item.poster, poster)
                title.text = item.title
                extra.isVisible = true
                clickableLayout.setOnClickListener {
                    listener.onClick(item)
                }
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Anime>() {
            override fun areItemsTheSame(oldItem: Anime, newItem: Anime) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Anime, newItem: Anime) =
                oldItem == newItem
        }
    }

    interface ItemClickListener {
        fun onClick(item: Anime)
    }

}