package com.letrix.anime.ui.player

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.letrix.anime.R
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.databinding.FragmentPlayerBinding
import com.letrix.anime.utils.Util.hideSystemUI
import com.letrix.anime.utils.Util.showSystemUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber.d


@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var player: SimpleExoPlayer? = null
    private val args by navArgs<PlayerFragmentArgs>()
    private lateinit var binding: FragmentPlayerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE

        activity?.hideSystemUI()

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
            if ((anime.totalEpisodes != null && anime.totalEpisodes == 1)/* || (args.episode.episode == 1)*/) {
                animeEpisode.text = anime.title
            } else animeEpisode.text = getString(R.string.episode_title, anime.title, 1)
        } else animeEpisode.text = getString(R.string.episode_title, anime.title, 1)
    }

    private fun getMediaSource(server: Server): MediaSource {
        val httpDataSourceFactory =
            DefaultHttpDataSourceFactory("Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19")

        d(server.toString())

        val referer = when (server.name) {
            "YourUpload" -> "https://www.yourupload.com/"
            "Streamtape", "Stape" -> "https://streamtape.com/"
            "Fembed" -> "https://fembed.com/"
            "Mail.ru" -> "https://my.mail.ru/"
            else -> ""
        }
        val cookie: String? = when (server.cookie) {
            null -> null
            else -> server.cookie
        }
        d(cookie.toString())
        httpDataSourceFactory.defaultRequestProperties["Referer"] = referer
        if (cookie != null) {
            httpDataSourceFactory.defaultRequestProperties["Cookie"] = "video_key=$cookie"
        }

        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(requireActivity(), null, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        return mediaSourceFactory.createMediaSource(MediaItem.fromUri(Uri.parse(server.selected().url)))
    }

    private fun initPlayer(server: Server) {

        activity?.hideSystemUI()
        val audioAttributes =
            AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build()
        player = SimpleExoPlayer.Builder(requireActivity()).setAudioAttributes(audioAttributes, true).build()
        binding.playerView.player = player

        player?.apply {
            addMediaSource(getMediaSource(server))
            prepare()
            seekTo(0, C.TIME_UNSET)
            playWhenReady = true
        }

        player?.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                d(error)
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

        }
        player?.release()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        activity?.showSystemUI()
    }

}