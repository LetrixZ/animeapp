package com.letrix.anime.ui.info.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.letrix.anime.data.Server
import com.letrix.anime.databinding.ItemChipBinding
import com.letrix.anime.databinding.ItemServerBinding
import com.letrix.anime.utils.Util

class ServerAdapter(private val serverClickListener: ServerClickListener) :
    ListAdapter<Server, ServerAdapter.ItemHolder>(object : DiffUtil.ItemCallback<Server>() {
        override fun areItemsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem.mirrors == newItem.mirrors
        }

    }) {
    inner class ItemHolder(
        private val binding: ItemServerBinding,
        private val clickListener: ServerClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(server: Server) {
            binding.serverName.text = server.name
            val mirrorClickListener = object : MirrorClickListener {
                override fun onItemClick(mirror: Int) {
                    clickListener.onServer(layoutPosition, mirror)
                }

            }
            val adapter = MirrorAdapter(mirrorClickListener)
            adapter.submitList(server.mirrors)
            binding.recyclerView.adapter = adapter
        }
    }

    interface ServerClickListener {
        fun onServer(server: Int, mirror: Int)
    }

    interface MirrorClickListener {
        fun onItemClick(mirror: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemServerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutManager = FlexboxLayoutManager(parent.context)
        layoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }
        binding.recyclerView.layoutManager = layoutManager
        return ItemHolder(binding, serverClickListener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MirrorAdapter(private val clickListener: MirrorClickListener) :
        ListAdapter<Server.Mirror, MirrorAdapter.ItemHolder>(object :
            DiffUtil.ItemCallback<Server.Mirror>() {
            override fun areItemsTheSame(oldItem: Server.Mirror, newItem: Server.Mirror): Boolean {
                return oldItem.quality == newItem.quality
            }

            override fun areContentsTheSame(
                oldItem: Server.Mirror,
                newItem: Server.Mirror
            ): Boolean {
                return oldItem.url == newItem.url
            }

        }) {
        inner class ItemHolder(
            private val binding: ItemChipBinding,
            private val clickListener: MirrorClickListener
        ) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(mirror: Server.Mirror) {
                binding.text.text = Util.parseQuality(mirror.quality)
                binding.clickableLayout.setOnClickListener {
                    clickListener.onItemClick(layoutPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemHolder(binding, clickListener)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) =
            holder.bind(getItem(position))

    }
}