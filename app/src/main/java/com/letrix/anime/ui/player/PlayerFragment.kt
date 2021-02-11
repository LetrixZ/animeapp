package com.letrix.anime.ui.player

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.databinding.FragmentPlayerBinding
import com.letrix.anime.ui.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.letrix.anime.room.Anime as AnimeRoom
import com.letrix.anime.room.Anime.Episode as EpisodeRoom

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var player: SimpleExoPlayer? = null
    private val args by navArgs<PlayerFragmentArgs>()
    private lateinit var binding: FragmentPlayerBinding
    private val roomViewModel by activityViewModels<RoomViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        view.doOnLayout {
            view.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            view.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding = FragmentPlayerBinding.bind(view)
        binding.aspectRatioLayout.setAspectRatio(16f / 9f)

        initPlayer(args.servers[args.selected])
        setPlayerInfo(args.anime)

        val toggleFullscreen: ToggleButton = view.findViewById(R.id.exo_fullscreen)
        toggleFullscreen.setOnClickListener {
            binding.playerView.resizeMode =
                if (toggleFullscreen.isChecked) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        val backButton: ImageView = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setPlayerInfo(anime: Anime) {
        val animeEpisode: TextView = view?.findViewById(R.id.title_episode)!!
        if (anime.type == "Pelicula") {
            if ((anime.totalEpisodes != null && anime.totalEpisodes == 1) || (args.episode.episode == 1)) {
                animeEpisode.text = anime.title
            } else animeEpisode.text = getString(R.string.episode_title, anime.title, args.episode.episode)
        } else animeEpisode.text = getString(R.string.episode_title, anime.title, args.episode.episode)
    }

    private fun initPlayer(server: Server) {
        val audioAttributes =
            AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build()
        player = SimpleExoPlayer.Builder(requireActivity()).setAudioAttributes(audioAttributes, true).build()
        binding.playerView.player = player

        Timber.d(args.episode.toString())

        val seekTime = if (args.episode.completed()) C.TIME_UNSET else args.episode.progress

        player?.apply {
            setMediaItem(MediaItem.Builder().setUri(Uri.parse(server.selected().url)).build())
            prepare()
            seekTo(0, seekTime)
            playWhenReady = true
        }

        player?.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Timber.d(error)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        binding.playerProgressBar.isVisible = true
                    }
                    Player.STATE_READY -> {
                        mediaReady = true
                        binding.playerProgressBar.isVisible = false
                        binding.playerView.keepScreenOn = true
                    }
                    Player.STATE_ENDED, Player.STATE_IDLE -> {
                        binding.playerView.keepScreenOn = false
                    }
                }
            }
        })
    }

    private var mediaReady = false

    override fun onDestroyView() {
        super.onDestroyView()
        if (player != null && mediaReady) {
            val episode = EpisodeRoom(
                animeId = args.anime.id,
                episode = args.episode.episode,
                progress = player?.currentPosition!!,
                duration = player?.duration!!
            )
            Timber.d(episode.toString())
            Timber.d(episode.completed().toString())
            val anime = AnimeRoom(
                args.anime,
                if (episode.completed() && episode.episode < args.anime.totalEpisodes!!) args.episode.episode + 1 else args.episode.episode
            )
            roomViewModel.upsert(episode, anime)
        }
        player?.release()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        view?.doOnLayout {
            view?.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            view?.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
    }

}