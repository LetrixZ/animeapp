package com.letrix.anime.ui.pager

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.letrix.anime.R
import com.letrix.anime.ui.list.ListFragment
import com.letrix.anime.ui.home.HomeFragment
import com.letrix.anime.ui.search.SearchFragment

class PagerAdapter(private val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val context = fragmentActivity
    private val titles = listOf(
        context.getString(R.string.home_title),
        context.getString(R.string.search_title),
        context.getString(R.string.genre_title),
        context.getString(R.string.year_title)
    )

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> HomeFragment()
        1 -> SearchFragment()
        2 -> ListFragment().also { it.arguments = bundleOf("type" to "genre") }
        3 -> ListFragment().also { it.arguments = bundleOf("type" to "year") }
        else -> Fragment()
    }

    fun getTabView(position: Int, onTabListener: OnTabListener): View? {
        val rootView = LayoutInflater.from(fragmentActivity).inflate(R.layout.item_chip, null)
        rootView.findViewById<TextView>(R.id.text).apply {
            text = titles[position]
            textSize = 16f
        }
        rootView.findViewById<CardView>(R.id.clickable_layout).setBackgroundResource(R.drawable.tab_selector)
        rootView.findViewById<CardView>(R.id.clickable_layout).setOnClickListener {
            onTabListener.onTab(position)
        }
        return rootView
    }

    fun unselectedTab(tab: TabLayout.Tab?) {
        tab?.customView
    }

    fun selectedTab(tab: TabLayout.Tab?) {
        tab?.customView
    }

    interface OnTabListener {
        fun onTab(position: Int)
    }
}