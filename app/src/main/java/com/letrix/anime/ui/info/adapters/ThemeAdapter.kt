package com.letrix.anime.ui.info.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.databinding.ItemEntryBinding
import com.letrix.anime.databinding.ItemThemeBinding

class ThemeAdapter(private val listener: OnThemeClickListener) :
    ListAdapter<AnimeThemes.Theme, ThemeAdapter.ItemHolder>(object : DiffUtil.ItemCallback<AnimeThemes.Theme>() {
        override fun areItemsTheSame(oldItem: AnimeThemes.Theme, newItem: AnimeThemes.Theme): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: AnimeThemes.Theme, newItem: AnimeThemes.Theme): Boolean {
            return oldItem == newItem
        }

    }) {
    inner class ItemHolder(private val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(theme: AnimeThemes.Theme) {
            binding.apply {
                titleType.text = "${theme.title} | ${theme.type}${theme.sequence ?: ""}"
                group.isVisible = theme.group.isNotEmpty().also {
                    if (it) group.text = theme.group
                }
                binding.entryRecycler.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                binding.entryRecycler.adapter = EntryAdapter(object : OnEntryClickListener {
                    override fun onEntry(selectedEntry: Int, selectedVideo: Int) {
                        theme.selectedVideo = selectedVideo
                        theme.selectedVersion = selectedEntry
                        listener.onTheme(theme)
                    }

                }).also {
                    it.submitList(theme.entries)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.bind(getItem(position))

    interface OnThemeClickListener {
        fun onTheme(theme: AnimeThemes.Theme)
    }

    interface OnEntryClickListener {
        fun onEntry(selectedEntry: Int, selectedVideo: Int)
    }


    inner class EntryAdapter(private val onEntryClickListener: OnEntryClickListener) :
        ListAdapter<AnimeThemes.Theme.Entry, EntryAdapter.ItemHolder>(object :
            DiffUtil.ItemCallback<AnimeThemes.Theme.Entry>() {
            override fun areItemsTheSame(oldItem: AnimeThemes.Theme.Entry, newItem: AnimeThemes.Theme.Entry): Boolean {
                return oldItem.slug == newItem.slug
            }

            override fun areContentsTheSame(oldItem: AnimeThemes.Theme.Entry, newItem: AnimeThemes.Theme.Entry): Boolean {
                return oldItem == newItem
            }

        }) {
        inner class ItemHolder(private val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(entry: AnimeThemes.Theme.Entry) {
                binding.version.text = "Version ${(entry.version ?: 1).toString()}"
                binding.episodes.text = "Episodes ${entry.episodes}"
                binding.nsfwFlair.isVisible = entry.nsfw
                binding.spoilerFlair.isVisible = entry.spoiler
                binding.videoList.adapter = ThemeSpinnerAdapter(binding.root.context, entry.videos)
                binding.root.setOnClickListener {
                    onEntryClickListener.onEntry(layoutPosition, binding.videoList.selectedItemPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryAdapter.ItemHolder {
            val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemHolder(binding)
        }

        override fun onBindViewHolder(holder: EntryAdapter.ItemHolder, position: Int) = holder.bind(getItem(position))
    }
}