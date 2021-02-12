package com.letrix.anime.ui.genre

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentGenreBinding
import com.letrix.anime.ui.AnimeAdapter
import com.letrix.anime.ui.search.LoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenreFragment : Fragment(R.layout.fragment_genre), GenrePagingAdapter.ItemClickListener {

    private lateinit var binding: FragmentGenreBinding
    private val args by navArgs<GenreFragmentArgs>()
    private val viewModel by activityViewModels<GenreViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGenreBinding.bind(view)

        val adapter = GenrePagingAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadStateAdapter { adapter.retry() },
                footer = LoadStateAdapter { adapter.retry() }
            )
            retryButton.setOnClickListener { adapter.retry() }
            recyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
                it.alignItems = AlignItems.CENTER
                it.justifyContent = JustifyContent.SPACE_EVENLY
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
                errorMessage.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    errorMessage.isVisible = true
                } else {
                    errorMessage.isVisible = false
                }
            }
        }

        viewModel.results.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

        /*viewModel.genre(args.genre)*/

    }
    override fun onClick(item: Anime) {
        findNavController().navigate(GenreFragmentDirections.actionGenreFragmentToInfoFragment(item.id, item))
    }

}