package com.letrix.anime.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentHomeBinding
import com.letrix.anime.ui.AnimeAdapter
import com.letrix.anime.ui.ScrollStateHolder
import com.letrix.anime.ui.home.adapter.ParentAdapter
import com.letrix.anime.ui.info.ServerBottomSheet
import com.letrix.anime.ui.pager.PagerFragmentDirections
import com.letrix.anime.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AnimeAdapter.ItemClickListener {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var scrollStateHolder: ScrollStateHolder

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var fullscreen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        scrollStateHolder = ScrollStateHolder(viewModel.bundle)

        setupObserver()

        /*binding.bookmarks.setOnClickListener {
            if (fullscreen) activity?.showSystemUI()
            else activity?.hideSystemUI()
            fullscreen = !fullscreen
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler(lists: List<Anime.List>) {

        val concatAdapter = ConcatAdapter()
        val titles = arrayListOf(
            getString(R.string.featured),
            getString(R.string.latest_added),
            getString(R.string.latest_series),
            getString(R.string.latest_movies),
            getString(R.string.latest_ovas)
        )

        lists.forEachIndexed { index, list ->
            concatAdapter.addAdapter(
                ParentAdapter(
                    AnimeAdapter(this, list.title).also { adapter -> adapter.submitList(list.list) },
                    titles[index],
                    scrollStateHolder
                )
            )
        }

        binding.recyclerView.apply {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun setupObserver() {
        viewModel.home.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) {
                        setupRecycler(it.data)
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.isVisible = true
                }
                Status.ERROR -> {
                    binding.progressBar.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.message
                }
            }
        })
    }


    override fun onPause() {
        super.onPause()
        if (this::scrollStateHolder.isInitialized) scrollStateHolder.onSaveInstanceState(viewModel.bundle)
    }

    override fun onAnime(item: Anime) {
        findNavController().navigate(PagerFragmentDirections.actionPagerFragmentToInfoFragment(item.id, item))
    }

    override fun onEpisode(episode: Int, item: Anime) {
        ServerBottomSheet().also {
            it.arguments = bundleOf("anime" to item, "episode" to episode)
            it.show(childFragmentManager, null)
        }
    }

}