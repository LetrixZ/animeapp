package com.letrix.anime.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.*
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.databinding.FragmentInfoNewBinding
import com.letrix.anime.utils.CustomPlayerUiController
import com.letrix.anime.utils.ImageLoader
import com.letrix.anime.utils.Status.*
import com.letrix.anime.utils.Util
import com.letrix.anime.utils.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info_new), YearAdapter.OnItemClickListener, EpisodeAdapter.OnItemClick {

    private val viewModel by viewModels<InfoViewModel>()
    private val args by navArgs<InfoFragmentArgs>()
    private lateinit var binding: FragmentInfoNewBinding
    private var anime: Anime? = null

    private var episodeLayoutManager: FlexboxLayoutManager? = null

    private var obtained = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoNewBinding.bind(view)

        binding.aspectRatioLayout.setAspectRatio(16f / 9f)

        episodeLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
            it.alignItems = AlignItems.CENTER
            it.justifyContent = JustifyContent.CENTER
        }
        episodeLayoutManager!!.onRestoreInstanceState(viewModel.bundle.getParcelable("layout"))

        if (viewModel.bundle.getParcelable<Anime>("anime") == null)
            setObservers()
        else {
            anime = viewModel.bundle.getParcelable<Anime>("anime") as Anime
            setupData(anime!!)
            getJikan(anime!!)
        }

        ImageLoader.loadImage(args.anime.banner!!, binding.banner)

        /*binding.markWatched.setOnClickListener {
            if (anime != null) {
                for (i in 1..anime!!.totalEpisodes!!) {
                    markWatched(i)
                }
            }
        }*/

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.bundle.putParcelable("layout", episodeLayoutManager!!.onSaveInstanceState())
        viewModel.bundle.putParcelable("anime", anime)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.bundle.remove("anime")
    }

    private fun setObservers() {
        viewModel.anime(args.id).observe(viewLifecycleOwner, {
            binding.progressBar.isVisible = false
            if (it != null) {
                anime = it
                setupData(it)
                obtained++
                getJikan(it)
                d("Obtained $obtained")
            } else {
                binding.progressBar.isVisible = false
                binding.errorMessage.isVisible = true
            }
        })
    }

    private fun loadPoster(poster: String) {
        try {
            ImageLoader.loadImage(poster, binding.poster)
            binding.playerLayout.isVisible = false
            binding.poster.isVisible = true
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.infoConstraint)
            constraintSet.connect(R.id.info_layout, ConstraintSet.TOP, R.id.poster, ConstraintSet.BOTTOM)
            constraintSet.applyTo(binding.infoConstraint)
        } catch (e: Exception) {
            e(e)
        }
    }

    private fun initPlayer(videoId: String) {
        lifecycle.addObserver(binding.youtubePlayerView)

        val iFramePlayerOption = IFramePlayerOptions.Builder()
            .controls(0)
            .rel(0)
            .ivLoadPolicy(3)
            .ccLoadPolicy(0)
            .build()

        val customUIView: View = binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui)

        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                binding.playerSeekbar.youtubePlayerSeekBarListener =
                    object : YouTubePlayerSeekBarListener {
                        override fun seekTo(time: Float) {
                            youTubePlayer.seekTo(time)
                        }
                    }

                youTubePlayer.mute()

                binding.volumeButton.isEnabled = true
                binding.volumeButton.setOnClickListener {
                    if (binding.volumeButton.isChecked) youTubePlayer.unMute()
                    else youTubePlayer.mute()
                }

                youTubePlayer.addListener(binding.playerSeekbar)
                val customPlayerUiController =
                    CustomPlayerUiController(requireContext(), customUIView, youTubePlayer, binding.youtubePlayerView)
                youTubePlayer.addListener(customPlayerUiController)
                binding.youtubePlayerView.addFullScreenListener(customPlayerUiController)
                youTubePlayer.loadVideo(videoId, 0f)

                binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                    if (Util.isVisible(binding.youtubePlayerView)) {
                        youTubePlayer.play()
                    } else {
                        youTubePlayer.pause()
                    }
                })
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
                super.onStateChange(youTubePlayer, state)
                binding.youtubeProgressBar.visibility = if (state == PlayerState.BUFFERING) View.VISIBLE else View.GONE
                if (state == PlayerState.PLAYING) {
                    binding.banner.animate().alpha(0f).setDuration(500).setInterpolator(AccelerateInterpolator()).start()
                }
            }
        }, false, iFramePlayerOption)

    }

    private fun getJikan(data: Anime) {
        if (data.malId != null)
            viewModel.getJikanAnime(data.malId).observe(viewLifecycleOwner, {
                d(it.toString())
                if (it != null) {
                    d(it.trailer)
                    if (it.trailer != null) {
                        val regex = Regex("""(?<=embed/).*(?=\?)""")
                        val videoId = regex.find(it.trailer)?.value
                        d(videoId.toString())
                        if (videoId != null) {
                            initPlayer(videoId)
                        }
                    } else loadPoster(it.poster)
                } else {
                    loadPoster(data.poster)
                }
            })
        else loadPoster(data.poster)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupData(data: Anime) {
            binding.apply {
                title.text = data.title
                if (data.synonyms!!.isEmpty()) synonyms.isVisible = false
                else {
                    synonyms.text = data.synonyms.joinToString("\n")
                }
                synopsis.text = data.synopsis!!
                state.text = if (data.state!! == 1) getString(R.string.ongoing) else getString(R.string.finished)
                state.setTextColor(ContextCompat.getColor(requireContext(), if (data.state == 1) R.color.green_500 else R.color.red_500))
                typeEpisodes.text = resources.getQuantityString(
                    R.plurals.type_episodes,
                    data.totalEpisodes!!,
                    if (data.type == "Anime") "Serie" else data.type,
                    data.totalEpisodes
                )
                if (data.nextEpisodeDate!!.isNotEmpty())
                    nextEpisode.text = SimpleDateFormat("E, dd MMM yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(data.nextEpisodeDate))
                else binding.nextEpisode.isVisible = false
                genres.adapter = YearAdapter(data.genres!!, object : YearAdapter.OnItemClickListener{
                    override fun onClick(year: String) {

                    }

                })
                genres.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).also {
                    it.alignItems = AlignItems.CENTER
                    it.justifyContent = JustifyContent.CENTER
                }

                val chunked = data.episodes?.chunked(21)?.map { list -> EpisodeList(list) }

                val episodeAdapter = EpisodeAdapter(this@InfoFragment, data.totalEpisodes)

                binding.episodesRecyclerView.adapter = episodeAdapter

                if (chunked != null) {
                    if (chunked.size != 1) {
                        binding.episodesSpinner.isVisible = true
                        binding.episodesSpinner.adapter = EpisodeSpinnerAdapter(requireContext(), chunked)
                    } else {
                        binding.episodesSpinner.isVisible = false
                    }
                    episodeAdapter.submitList(chunked[0].list)

                    binding.episodesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            episodeAdapter.submitList(chunked[position].list)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                    }

                    episodesRecyclerView.layoutManager = episodeLayoutManager
                }


            }
    }

    override fun onClick(year: String) {
//        findNavController().navigate(InfoFragmentDirections.actionInfoFragmentToGenreFragment(genre.replace(" ", "-")))
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
//        markWatched(episode)
    }

    data class EpisodeList(
        val list: List<Anime.Episode>
    )

}