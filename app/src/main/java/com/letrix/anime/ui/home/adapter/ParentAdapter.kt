package com.letrix.anime.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letrix.anime.databinding.ItemRowBinding
import com.letrix.anime.ui.ScrollStateHolder

class ParentAdapter(
    private val adapter: RecyclerView.Adapter<*>,
    private val headerText: String,
    private val scrollStateHolder: ScrollStateHolder
) : RecyclerView.Adapter<ParentAdapter.ItemHolder>() {
    inner class ItemHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root), ScrollStateHolder.ScrollStateKeyProvider {
        fun onCreate() {
            scrollStateHolder.setupRecyclerView(binding.recyclerView, this)
        }

        fun onBind() {
            binding.recyclerView.adapter = adapter
            binding.itemHeader.text = headerText
            scrollStateHolder.restoreScrollState(binding.recyclerView, this)
        }

        fun onRecycled() {
            scrollStateHolder.saveScrollState(binding.recyclerView, this)
        }

        override fun getScrollStateKey(): String = headerText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = ItemHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        itemHolder.onCreate()
        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.onBind()

    override fun getItemCount(): Int = 1

    override fun onViewRecycled(holder: ItemHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }
}