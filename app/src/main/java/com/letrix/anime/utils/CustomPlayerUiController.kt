package com.letrix.anime.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import com.letrix.anime.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper

internal class CustomPlayerUiController(
    private val context: Context,
    private val view: View,
    private val youTubePlayer: YouTubePlayer,
    private val youTubePlayerView: YouTubePlayerView
) : AbstractYouTubePlayerListener(), YouTubePlayerFullScreenListener, YouTubePlayerSeekBarListener {

    private val playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()
    private var fullscreen = false

    private lateinit var panel: View
    private lateinit var youtubePlayerSeekBar: YouTubePlayerSeekBar
    private lateinit var playPauseButton: ToggleButton
    private lateinit var fullscreenButton: ToggleButton
    private lateinit var volumeButton: ToggleButton

    private fun initViews() {

        val fadeViewHelper = FadeViewHelper(view.findViewById(R.id.control_layout))
        fadeViewHelper.animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY

        youTubePlayer.addListener(fadeViewHelper)

        panel = view.findViewById(R.id.panel)
        youtubePlayerSeekBar = view.findViewById(R.id.youtube_player_seekbar)
        playPauseButton = view.findViewById(R.id.play_pause_button)
        fullscreenButton = view.findViewById(R.id.fullscreen_button)
        volumeButton = view.findViewById(R.id.volume_button)

        youtubePlayerSeekBar.youtubePlayerSeekBarListener = this
        youTubePlayer.addListener(youtubePlayerSeekBar)

        playPauseButton.setOnClickListener {
            if (playPauseButton.isChecked) {
                youTubePlayer.play()
            } else {
                youTubePlayer.pause()
            }
        }

        fullscreenButton.setOnClickListener {
            if (fullscreen) youTubePlayerView.exitFullScreen() else youTubePlayerView.enterFullScreen()
            fullscreen = !fullscreen
        }

        volumeButton.setOnClickListener {
            if (volumeButton.isChecked) youTubePlayer.mute()
            else youTubePlayer.unMute()
        }

        panel.setOnClickListener { fadeViewHelper.toggleVisibility() }
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
        if (state == PlayerState.PLAYING || state == PlayerState.PAUSED || state == PlayerState.VIDEO_CUED) {
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        } else if (state == PlayerState.BUFFERING) panel.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        ) else if (state == PlayerState.ENDED) {
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
        }
        if (state == PlayerState.PLAYING) playPauseButton.isChecked = true
        else if (state == PlayerState.PAUSED || state == PlayerState.BUFFERING || state == PlayerState.ENDED) playPauseButton.isChecked = false
    }

    override fun onYouTubePlayerEnterFullScreen() {
        val viewParams = view.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams = viewParams
    }

    override fun onYouTubePlayerExitFullScreen() {
        val viewParams = view.layoutParams
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams = viewParams
    }

    override fun seekTo(time: Float) {
        youTubePlayer.seekTo(time)
    }

    init {
        youTubePlayer.addListener(playerTracker)
        initViews()
    }
}