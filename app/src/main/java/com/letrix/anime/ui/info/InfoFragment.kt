package com.letrix.anime.ui.info

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentInfoBinding
import com.letrix.anime.ui.RoomViewModel
import com.letrix.anime.ui.genre.GenreViewModel
import com.letrix.anime.utils.ImageLoader
import com.letrix.anime.utils.Status.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.*

@ExperimentalStdlibApi
@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info), GenreAdapter.OnItemClickListener, EpisodeAdapter.OnItemClick {

    private val viewModel by viewModels<InfoViewModel>()
    private val roomViewModel by activityViewModels<RoomViewModel>()
    private val args by navArgs<InfoFragmentArgs>()
    private lateinit var binding: FragmentInfoBinding
    private var anime: Anime? = null

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

        ImageLoader.loadImage(args.anime.poster, binding.poster)

        binding.markWatched.setOnClickListener {
            if (anime != null) {
                for (i in 1..anime!!.totalEpisodes!!) {
                    markWatched(i)
                }
            }
        }

    }

    private fun markWatched(episodeNumber: Int) {
        d(episodeNumber.toString())
        val episode = com.letrix.anime.room.Anime.Episode(
            animeId = args.anime.id,
            episode = episodeNumber,
            progress = 100000L,
            duration = 100100L
        )
        val anime = com.letrix.anime.room.Anime(
            anime!!,
            episodeNumber
        )
        /*d((if (episode.completed() && episode.episode < args.anime.totalEpisodes!!) episodeNumber + 1 else episodeNumber).toString())*/
        roomViewModel.upsert(episode, anime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bundle.putParcelable("layout", episodeLayoutManager!!.onSaveInstanceState())
    }

    private fun setObservers() {
        viewModel.info.observe(viewLifecycleOwner, {
            when (it.status) {
                SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) {
                        anime = it.data
                        setupData(it.data)
                    }
                }
                LOADING -> {
                    binding.progressBar.isVisible = true
                }
                ERROR -> {
                    binding.progressBar.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.message
                }
            }
        })
    }

    private fun setupData(data: Anime) {
        roomViewModel.get(data.id).observe(viewLifecycleOwner, {
            binding.apply {
                title.text = data.title
                synonyms.text = data.synonyms!!.joinToString("\n")
                synopsis.text = data.synopsis!!
                state.text = if (data.ongoing!!) getString(R.string.ongoing) else getString(R.string.finished)
                state.setTextColor(ContextCompat.getColor(requireContext(), if (data.ongoing) R.color.green_500 else R.color.red_500))
                typeEpisodes.text = resources.getQuantityString(R.plurals.type_episodes, data.totalEpisodes!!, data.type, data.totalEpisodes)
                genres.adapter = GenreAdapter(data.genres!!, this@InfoFragment)
                genres.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
                    it.alignItems = AlignItems.CENTER
                    it.justifyContent = JustifyContent.CENTER
                }
                episodes.adapter = EpisodeAdapter(buildList {
                    for (i in 1..data.totalEpisodes) {
                        add(element = getString(R.string.episode_ns, i.toString().padStart(data.totalEpisodes.toString().length, '0')))
                    }
                }, it?.episodes?.map { episode ->
                    Pair(episode.episode, episode)
                }?.toMap(), this@InfoFragment)
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
        })
    }

    override fun onClick(genre: String) {
        val genreViewModel by activityViewModels<GenreViewModel>()
        genreViewModel.genre(genre)
        findNavController().navigate(InfoFragmentDirections.actionInfoFragmentToGenreFragment(genre.replace(" ", "-")))
    }

    private fun showServers(episode: Int) {
        ServerBottomSheet().also {
            it.arguments = bundleOf("anime" to anime!!, "episode" to episode)
            it.show(childFragmentManager, null)
        }
    }

    override fun onEpisode(episode: Int) {
        showServers(episode)
    }

    override fun onEpisodeLong(episode: Int) {
        markWatched(episode)
    }

}