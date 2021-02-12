package com.letrix.anime.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.databinding.ItemChipBinding
import com.letrix.anime.room.Anime
import com.letrix.anime.utils.Util

class EpisodeAdapter(private val list: List<String>, private val watched: Map<Int, Anime.Episode>?, private val clickListener: OnItemClick) :
    RecyclerView.Adapter<EpisodeAdapter.ItemHolder>() {
    inner class ItemHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(episode: String) {
            binding.text.text = episode
            binding.text.background.level = Util.getProgress(watched?.get(layoutPosition + 1))
            binding.clickableLayout.setOnClickListener {
                clickListener.onEpisode(layoutPosition + 1)
            }
            binding.clickableLayout.setOnLongClickListener {
                clickListener.onEpisodeLong(layoutPosition + 1)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        holder.onBind(list[position])

    override fun getItemCount(): Int = list.size

    interface OnItemClick {
        fun onEpisode(episode: Int)
        fun onEpisodeLong(episode: Int)
    }
}