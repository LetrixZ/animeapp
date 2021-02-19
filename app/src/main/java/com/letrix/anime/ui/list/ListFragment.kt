package com.letrix.anime.ui.list

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Genre
import com.letrix.anime.databinding.FragmentGenreBinding
import com.letrix.anime.ui.info.GenreAdapter
import com.letrix.anime.ui.info.YearAdapter
import com.letrix.anime.ui.pager.PagerFragmentDirections
import com.letrix.anime.ui.search.LoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber.d

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), ListPagingAdapter.ItemClickListener {

    private lateinit var binding: FragmentGenreBinding
    private val args by navArgs<ListFragmentArgs>()
    private val viewModel by viewModels<ListViewModel>()

    private var isShown = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGenreBinding.bind(view)

        val adapter = ListPagingAdapter(this)

        when (args.type) {
            "year" -> {
                setYearList()
                viewModel.resultsYear.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        adapter.submitData(it)
                    }
                })
            }
            "genre" -> {
                setGenreList()
                viewModel.resultsGenre.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        adapter.submitData(it)
                    }
                })
            }
        }

        binding.apply {
            itemsRecycler.setHasFixedSize(true)
            itemsRecycler.itemAnimator = null
            itemsRecycler.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadStateAdapter { adapter.retry() },
                footer = LoadStateAdapter { adapter.retry() }
            )
            retryButton.setOnClickListener { adapter.retry() }
            title.text = ""
            title.setOnClickListener {
                showList()
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                /*itemsRecycler.isVisible = loadState.source.refresh is LoadState.NotLoading*/
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
                errorMessage.isVisible = loadState.source.refresh is LoadState.Error
                errorLayout.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading) {
                    if (!isShown) {
                        d("isShown!")
                        binding.itemsRecycler.isVisible = true
                        binding.itemsRecycler.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right_2))
                        isShown = true
                    }
                }

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    errorLayout.isVisible = true
                    itemsRecycler.isVisible = false
                    errorMessage.isVisible = true
                } else {
                    errorMessage.isVisible = false
                }
            }
        }
    }

    private fun setYearList() {
        viewModel.yearList.observe(viewLifecycleOwner, {
            if (it != null) {
                val adapter = YearAdapter(it, object : YearAdapter.OnItemClickListener {
                    override fun onClick(year: String) {
                        viewModel.year(year.toInt())
                        binding.title.text = year
                        hideList()
                    }
                })
                binding.selectorRecycler.adapter = adapter
                binding.selectorRecycler.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also { flexbox ->
                    flexbox.alignItems = AlignItems.CENTER
                    flexbox.justifyContent = JustifyContent.FLEX_START
                }
            }
        })
    }

    private fun setGenreList() {
        viewModel.genreList.observe(viewLifecycleOwner, {
            if (it != null) {
                val adapter = GenreAdapter(it, object : GenreAdapter.OnItemClickListener {
                    override fun onClick(genre: Genre) {
                        viewModel.genre(genre.value)
                        binding.title.text = genre.title
                        hideList()
                    }
                })
                binding.selectorRecycler.adapter = adapter
                binding.selectorRecycler.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also { flexbox ->
                    flexbox.alignItems = AlignItems.CENTER
                    flexbox.justifyContent = JustifyContent.FLEX_START
                }
            }
        })
    }

    private fun showList() {
        binding.itemsRecycler.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right_2))
        binding.itemsRecycler.isVisible = false
        binding.title.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right_2))
        binding.title.isVisible = false

        binding.errorLayout.isVisible = false
        binding.selectorRecycler.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left_2))
        binding.selectorRecycler.isVisible = true
    }

    private fun hideList() {
        binding.selectorRecycler.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left_2))
        binding.selectorRecycler.isVisible = false

        binding.title.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right_2))
        binding.title.isVisible = true

        isShown = false
        d("changing to false")
    }

    override fun onClick(item: Anime) {
        findNavController().navigate(PagerFragmentDirections.actionPagerFragmentToInfoFragment(item.id, item))
    }

}