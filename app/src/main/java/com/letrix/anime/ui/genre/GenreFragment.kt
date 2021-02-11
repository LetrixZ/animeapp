package com.letrix.anime.ui.genre

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentGenreBinding
import com.letrix.anime.ui.AnimeAdapter
import com.letrix.anime.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.e

@AndroidEntryPoint
class GenreFragment : Fragment(R.layout.fragment_genre), AnimeAdapter.ItemClickListener {

    private lateinit var binding: FragmentGenreBinding
    private val args by navArgs<GenreFragmentArgs>()
    private val viewModel by viewModels<GenreViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGenreBinding.bind(view)

        if (viewModel.genre.value == null) {
            setupObservers()
            viewModel.genre(args.genre)
        } else {
            setupRecycler(viewModel.genre.value!!.data!!)
        }
    }

    private fun setupRecycler(list: Anime.List) {
        binding.apply {
            genre.text = list.title
            recyclerView.adapter = AnimeAdapter(this@GenreFragment, "genre").also { it.submitList(list.list) }
            recyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
                it.alignItems = AlignItems.CENTER
                it.justifyContent = JustifyContent.SPACE_EVENLY
            }
        }
    }

    private fun setupObservers() {
        viewModel.genre.observe(viewLifecycleOwner, {
            when (it.status) {
                SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) setupRecycler(it.data)
                }
                ERROR -> {
                    e(it.message)
                    binding.progressBar.isVisible = false
                }
                LOADING -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onAnime(item: Anime) {
        findNavController().navigate(GenreFragmentDirections.actionGenreFragmentToInfoFragment(item.id))
    }

    override fun onEpisode(episode: Int, item: Anime) {
    }

}