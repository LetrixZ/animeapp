package com.letrix.anime.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentHomeBinding
import com.letrix.anime.ui.AnimeAdapter
import com.letrix.anime.ui.RoomViewModel
import com.letrix.anime.ui.ScrollStateHolder
import com.letrix.anime.ui.home.adapter.ParentAdapter
import com.letrix.anime.ui.info.ServerBottomSheet
import com.letrix.anime.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.d

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AnimeAdapter.ItemClickListener {

    private val viewModel by viewModels<HomeViewModel>()
    private val roomViewModel by activityViewModels<RoomViewModel>()
    private lateinit var scrollStateHolder: ScrollStateHolder

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        scrollStateHolder = ScrollStateHolder(viewModel.bundle)

        setupObserver()

        binding.search.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionFragmentHomeToSearchFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler(list: List<Anime.List>) {
        roomViewModel.list.observe(viewLifecycleOwner, {
            val concatAdapter = ConcatAdapter()

            if (it != null) {
                val trackedList = it.filter { tracked -> !tracked.anime.completed }.map { tracked ->
                    val latestEpisode = tracked.episodes.filter { episode -> episode.episode == tracked.anime.latestEpisode }.get(0)
                    Anime(
                        id = tracked.anime.id,
                        title = tracked.anime.title,
                        poster = tracked.anime.poster,
                        totalEpisodes = tracked.anime.totalEpisodes,
                        ongoing = tracked.anime.ongoing,
                        latestEpisode = tracked.anime.latestEpisode/*if (latestEpisode.completed()) tracked.anime.latestEpisode + 1 else tracked.anime.latestEpisode*/
                    ).also { anime ->
                        anime.watched = tracked.episodes.map { episode -> episode.episode }
                        anime.nextEpisode = if (latestEpisode.completed()) tracked.anime.latestEpisode + 1 else tracked.anime.latestEpisode
                    }
                }
                if (trackedList.isNotEmpty()) {
                    concatAdapter.addAdapter(
                        ParentAdapter(
                            AnimeAdapter(this, "tracked").also { adapter -> adapter.submitList(trackedList) },
                            getString(R.string.continue_watching),
                            scrollStateHolder
                        )
                    )
                }
            }

            val titles = arrayListOf(
                getString(R.string.featured),
                getString(R.string.other_anime),
                getString(R.string.top_anime),
                getString(R.string.latest_added),
                getString(R.string.movies),
                getString(R.string.ovas)
            )

            list.forEachIndexed { index, list ->
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

        })
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
        findNavController().navigate(HomeFragmentDirections.actionFragmentHomeToInfoFragment(item.id, item))
    }

    override fun onEpisode(episode: Int, item: Anime) {
        ServerBottomSheet().also {
            it.arguments = bundleOf("anime" to item, "episode" to episode)
            it.show(childFragmentManager, null)
        }
    }

}