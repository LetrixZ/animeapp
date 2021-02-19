package com.letrix.anime.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.ItemAnimeRowBinding
import com.letrix.anime.utils.ImageLoader


class SearchPagingAdapter(private val listener: ItemClickListener) :
    PagingDataAdapter<Anime, SearchPagingAdapter.ItemHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemAnimeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ItemHolder(private val binding: ItemAnimeRowBinding) :
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
            val context = binding.root.context
            binding.apply {
                ImageLoader.loadImage(item.poster, poster)
                title.text = item.title
//                typeEpisodes.text = binding.root.context.resources.getQuantityString(R.plurals.type_episodes, item.totalEpisodes!!, item.type, item.totalEpisodes)
                typeEpisodes.text = when (item.type) {
                    "Anime" -> context.getString(R.string.serie)
                    "Película" -> context.getString(R.string.movie)
                    "OVA" -> context.getString(R.string.ova)
                    "Especial" -> context.getString(R.string.special)
                    else -> ""
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