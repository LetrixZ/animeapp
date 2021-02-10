package com.letrix.anime.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentInfoBinding
import com.letrix.anime.utils.ImageLoader
import com.letrix.anime.utils.Status
import com.letrix.anime.ui.info.InfoFragmentArgs
import com.letrix.anime.ui.info.InfoFragmentDirections
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.*

@ExperimentalStdlibApi
@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info), GenreAdapter.OnItemClickListener, EpisodeAdapter.OnItemClick {

    private val viewModel by viewModels<InfoViewModel>()
    private val args by navArgs<InfoFragmentArgs>()
    private lateinit var binding: FragmentInfoBinding

    private var episodeLayoutManager: FlexboxLayoutManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)

        episodeLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
            it.alignItems = AlignItems.CENTER
            it.justifyContent = JustifyContent.CENTER
        }
        episodeLayoutManager!!.onRestoreInstanceState(viewModel.bundle.getParcelable("layout"))

        viewModel.info(args.id)
        setObservers()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bundle.putParcelable("layout", episodeLayoutManager!!.onSaveInstanceState())
    }

    private fun setObservers() {
        viewModel.info.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) {
                        setupData(it.data)
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

    @SuppressLint("SetTextI18n")
    private fun setupData(data: Anime) {
        d(data.toString())
        binding.apply {
            ImageLoader.loadImage(data.poster, poster)
            title.text = data.title
            synonyms.text = data.synonyms!!.joinToString("\n")
            synopsis.text = data.synopsis!!
            state.text = if (data.ongoing!!) getString(R.string.ongoing) else getString(R.string.finished)
            state.setTextColor(ContextCompat.getColor(requireContext(), if (data.ongoing) R.color.green_500 else R.color.red_500))
            typeEpisodes.text = "${data.type!!} | ${data.totalEpisodes!!} episodios"
            genres.adapter = GenreAdapter(data.genres!!, this@InfoFragment)
            genres.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
                it.alignItems = AlignItems.CENTER
                it.justifyContent = JustifyContent.CENTER
            }
            episodes.adapter = EpisodeAdapter(buildList {
                for (i in 1..data.totalEpisodes) {
                    add(element = getString(R.string.episode_n, i.toString().padStart(data.totalEpisodes.toString().length, '0')))
                }
            }, this@InfoFragment)
            episodes.layoutManager = episodeLayoutManager
            root.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
                override fun onPanelSlide(panel: View?, slideOffset: Float) {
                    binding.layoutArrow.rotation = slideOffset.times(180)
                }

                override fun onPanelStateChanged(
                    panel: View?,
                    previousState: SlidingUpPanelLayout.PanelState?,
                    newState: SlidingUpPanelLayout.PanelState?
                ) {
                }

            })
        }
    }

    override fun onClick(genre: String) {
        findNavController().navigate(InfoFragmentDirections.actionInfoFragmentToGenreFragment(genre.replace(" ", "-")))
    }

    override fun onEpisode(episodeId: String) {

    }

}