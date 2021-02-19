package com.letrix.anime.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.ItemChipBinding
import com.letrix.anime.utils.Util

class EpisodeAdapter(
    private val clickListener: OnItemClick,
    private val totalEpisodes: Int
) :
    ListAdapter<Anime.Episode, EpisodeAdapter.ItemHolder>(object : DiffUtil.ItemCallback<Anime.Episode>() {
        override fun areItemsTheSame(oldItem: Anime.Episode, newItem: Anime.Episode): Boolean {
            return oldItem.episode == newItem.episode
        }

        override fun areContentsTheSame(oldItem: Anime.Episode, newItem: Anime.Episode): Boolean {
            return oldItem == newItem
        }

    }) {
    inner class ItemHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(episode: Anime.Episode) {
            binding.text.text =
                binding.root.context.getString(R.string.episode_ns, episode.episode.toString().padStart(totalEpisodes.toString().length, '0'))
//            binding.text.background.level = Util.getProgress(watched?.get(episode.episode))
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
        holder.onBind(getItem(position)/*list.list[position]*/)

//    override fun getItemCount(): Int = list.list.size

    interface OnItemClick {
        fun onEpisode(episode: Int)
        fun onEpisodeLong(episode: Int)
    }
}