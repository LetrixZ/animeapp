package com.letrix.anime.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.ItemAnimeBinding
import com.letrix.anime.utils.ImageLoader
import com.letrix.anime.utils.Util

class AnimeAdapter(private val itemClickListener: ItemClickListener, private val title: String) :
    ListAdapter<Anime, AnimeAdapter.ItemHolder>(object : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAnimeBinding.bind(itemView)
        fun onBind(item: Anime) {
            binding.apply {
                title.text = item.title
                if (this@AnimeAdapter.title == "featured") ImageLoader.loadImage(item.thumbnail!!, poster)
                else ImageLoader.loadImage(item.poster, poster)
            }
            when (title) {
                "top" -> {
                    binding.extra.isVisible = true
                    binding.extra.text = itemView.context.getString(R.string.likes_n, Util.getLikes(item.likes))
                    binding.clickableLayout.setOnClickListener { itemClickListener.onAnime(item) }
                }
                "tracked" -> {
                    binding.extra.isVisible = true
                    if (item.totalEpisodes == item.watched.size && item.latestEpisode == item.totalEpisodes) {
                        binding.extra.text = itemView.context.getString(R.string.next_episode, item.nextEpisode)
                    } else binding.extra.text = itemView.context.getString(R.string.episode_n, item.latestEpisode)
                    binding.clickableLayout.setOnClickListener { itemClickListener.onAnime(item) }
                }
                "featured", "other" -> {
                    binding.extra.isVisible = true
                    binding.extra.text = itemView.context.getString(R.string.episode_n, item.latestEpisode)
                    binding.clickableLayout.setOnClickListener { itemClickListener.onEpisode(item.latestEpisode!!, item) }
                }
                else -> {
                    binding.clickableLayout.setOnClickListener { itemClickListener.onAnime(item) }
                }
            }
        }
    }

    interface ItemClickListener {
        fun onAnime(item: Anime)
        fun onEpisode(episode: Int, item: Anime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val layout = when (title) {
            "featured" -> R.layout.item_anime_episode_thumbnail
            else -> R.layout.item_anime
        }
        return ItemHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}