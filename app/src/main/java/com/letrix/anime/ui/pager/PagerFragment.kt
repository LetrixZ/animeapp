package com.letrix.anime.ui.pager

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.letrix.anime.R
import com.letrix.anime.databinding.FragmentPagerBinding


class PagerFragment : Fragment(R.layout.fragment_pager), PagerAdapter.OnTabListener {

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var binding: FragmentPagerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPagerBinding.bind(view)
        pagerAdapter = PagerAdapter(requireActivity())
        binding.pager.adapter = pagerAdapter

        binding.pager.offscreenPageLimit = 5

        binding.search.setOnClickListener {
            findNavController().navigate(PagerFragmentDirections.actionPagerFragmentToSearchFragment())
        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
//            tab.text = "Home"
            tab.view.isClickable = false
            tab.customView = pagerAdapter.getTabView(position, this)
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                pagerAdapter.selectedTab(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                pagerAdapter.unselectedTab(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }

    override fun onTab(position: Int) {
        binding.pager.setCurrentItem(position, true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // And when you want to go back based on your condition
                if (binding.pager.currentItem == 0) {
                    this.isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    binding.pager.setCurrentItem(0, true)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}