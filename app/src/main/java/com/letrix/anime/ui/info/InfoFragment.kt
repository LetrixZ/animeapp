package com.letrix.anime.ui.info

import android.animation.Animator
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.*
import com.google.android.flexbox.*
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.databinding.FragmentInfoExpandableBinding
import com.letrix.anime.ui.info.adapters.EpisodeAdapter
import com.letrix.anime.ui.info.adapters.EpisodeSpinnerAdapter
import com.letrix.anime.ui.info.adapters.ThemeAdapter
import com.letrix.anime.ui.info.adapters.YearAdapter
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
import net.cachapa.expandablelayout.ExpandableLayout
import timber.log.Timber.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info_expandable), YearAdapter.OnItemClickListener, EpisodeAdapter.OnItemClick,
    ThemeAdapter.OnThemeClickListener {

    private val viewModel by viewModels<InfoViewModel>()
    private val args by navArgs<InfoFragmentArgs>()
    private lateinit var binding: FragmentInfoExpandableBinding
    private var anime: Anime? = null

    private var episodeLayoutManager: FlexboxLayoutManager? = null

    private var obtained = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchInfo(args.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoExpandableBinding.bind(view)
        binding.expandableCard.setOnClickListener {
            binding.expandable.toggle()
        }
        binding.expandable.setOnExpansionUpdateListener { _, state ->
            when (state) {
                ExpandableLayout.State.EXPANDED, ExpandableLayout.State.EXPANDING -> binding.expandableButton.isChecked = true
                ExpandableLayout.State.COLLAPSED, ExpandableLayout.State.COLLAPSING -> binding.expandableButton.isChecked = false
            }
        }
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
            getThemes(anime!!)
            getJikan(anime!!)
        }

        args.anime.banner?.let { ImageLoader.loadImage(it, binding.banner) }

        binding.themesExpandableCard.setOnClickListener {
            binding.themesExpandable.toggle()
        }

        /*binding.themesExpandable.setOnExpansionUpdateListener { _, state ->
            if (state == ExpandableLayout.State.EXPANDED) {
                anime?.let { getThemes(it) }
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
        viewModel.bundle.remove("themes")
        viewModel.bundle.remove("themes_available")
    }

    private fun setObservers() {
        viewModel.info.observe(viewLifecycleOwner, {
            when (it.status) {
                SUCCESS -> {
                    binding.progressBar.isVisible = false
                    if (it.data != null) {
                        anime = it.data
                        setupData(it.data)
                        getJikan(it.data)
//                        getThemes(it.data)
                        obtained++
                        d("Obtained $obtained")
                    }
                }
                ERROR -> {
                    loadPoster(null)
                    binding.progressBar.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.message
                    binding.themesExpandableCard.isClickable = false
                    binding.expandableCard.isClickable = false
                    d(it.message)
                }
                LOADING -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun loadPoster(poster: String?) {
        try {
            poster?.let { ImageLoader.loadImage(it, binding.poster) }
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

                binding.replayButton.setOnClickListener {
                    youTubePlayer.seekTo(0f)
                    youTubePlayer.play()
                }

                binding.playerSeekbar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
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
                val customPlayerUiController = CustomPlayerUiController(requireContext(), customUIView, youTubePlayer, binding.youtubePlayerView)
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
                    binding.replayButton.isVisible = false
                    binding.banner.animate().alpha(0f).setDuration(500).setInterpolator(AccelerateInterpolator()).setListener(object :
                        Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {}

                        override fun onAnimationEnd(animation: Animator?) {
                            binding.banner.isVisible = false
                        }

                        override fun onAnimationCancel(animation: Animator?) {}

                        override fun onAnimationRepeat(animation: Animator?) {}

                    }).start()
                } else if (state == PlayerState.ENDED) {
                    binding.replayButton.isVisible = true
                    binding.banner.animate().alpha(1f).setDuration(500).setInterpolator(AccelerateInterpolator()).setListener(object :
                        Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                            binding.banner.isVisible = true
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                        }

                        override fun onAnimationCancel(animation: Animator?) {}

                        override fun onAnimationRepeat(animation: Animator?) {}

                    }).start()
                }
            }
        }, false, iFramePlayerOption)

    }

    private var hasThemes: Boolean? = null

    @SuppressLint("SetTextI18n")
    private fun getThemes(data: Anime) {
        val themes = viewModel.bundle.getParcelable<AnimeThemes>("themes")
        if (themes == null && hasThemes == null) {
            if (data.malId != null) {
                viewModel.themes(data.malId).observe(viewLifecycleOwner, {
                    d(it.toString())
                })
            } else {
                viewModel.fetchThemes(data.title)
                viewModel.themes.observe(viewLifecycleOwner, {
                    when (it.status) {
                        SUCCESS -> {
                            if (it.data != null) {
                                binding.themesProgressBar.isVisible = false
                                setThemes(it.data)
                                viewModel.bundle.putParcelable("themes", it.data)
                                hasThemes = true
                            }
                        }
                        ERROR -> {
                            d(it.message.toString())
                            binding.themesProgressBar.isVisible = false
                            binding.themesErrorMessage.isVisible = true
                            if (it.message?.contains("Not Found") == true) {
                                binding.themesErrorMessage.text = "This show doesn't have themes"
                                hasThemes = false
                            } else {
                                binding.themesErrorMessage.text = it.message
                            }
                        }
                        LOADING -> {
                            binding.themesProgressBar.isVisible = true
                        }
                    }
                })
            }
        } else {
            binding.themesProgressBar.isVisible = false
            if (hasThemes == true) {
                if (themes != null) {
                    setThemes(themes)
                }
            } else {
                binding.themesErrorMessage.isVisible = true
                binding.themesErrorMessage.text = "This show doesn't have themes"
            }
        }
    }

    private fun setThemes(data: AnimeThemes) {
        binding.themesRecycler.adapter = ThemeAdapter(this).also {
            it.submitList(data.themes)
        }
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

            synopsis.text = data.synopsis
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
            genres.adapter = YearAdapter(data.genres!!, object : YearAdapter.OnItemClickListener {
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

    override fun onTheme(theme: AnimeThemes.Theme) {
        d(theme.toString())
    }

}