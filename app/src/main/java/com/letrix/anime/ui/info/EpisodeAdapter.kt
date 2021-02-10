package com.letrix.anime.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.databinding.ItemChipBinding

class EpisodeAdapter(private val list: List<String>, private val clickListener: OnItemClick) :
    RecyclerView.Adapter<EpisodeAdapter.ItemHolder>() {
    inner class ItemHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(episode: String) {
            binding.root.text = episode
            binding.root.isCheckable = false
            binding.root.setOnClickListener {
                clickListener.onEpisode(layoutPosition + 1)
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
    }
}