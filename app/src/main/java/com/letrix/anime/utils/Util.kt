package com.letrix.anime.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Base64
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.letrix.anime.R
import com.letrix.anime.room.Anime
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

object Util {

    fun getProgress(episode: Anime.Episode?): Int {
        return if (episode != null) {
            if (episode.completed()) 10000
            else if ((episode.progress * 100 / episode.duration) <= 5) 1000
            else (episode.progress * 10000 / episode.duration).toInt()
        } else 0
    }


    fun encodeResponse(msg: String): String {
        val data = msg.toByteArray(StandardCharsets.UTF_8)
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun getTitle(title: String, context: Context): String {
        return when (title) {
            "featured" -> context.getString(R.string.featured)
            "other" -> context.getString(R.string.other_anime)
            "top" -> context.getString(R.string.top_anime)
            "latest" -> context.getString(R.string.latest_added)
            "movies" -> context.getString(R.string.movies)
            "ovas" -> context.getString(R.string.ovas)
            else -> title
        }
    }

    fun parseQuality(item: String): String {
        return when (item.toLowerCase(Locale.ROOT)) {
            "720p", "default", "hd" -> return "Calidad alta (720p)"
            "480p", "sd" -> return "Calidad media (480p)"
            "low" -> return "Calidad baja (360p)"
            "lowest" -> return "Calidad muy baja (240p)"
            "mobile" -> return "No te molestes (144p)"
            else -> "Default"
        }
    }

    fun getLikes(count: Int?): String {
        if (count == null) return ""
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1]).replace(",", ".")
    }

    /**
     * Hides the system bars and makes the Activity "fullscreen". If this should be the default
     * state it should be called from [Activity.onWindowFocusChanged] if hasFocus is true.
     * It is also recommended to take care of cutout areas. The default behavior is that the app shows
     * in the cutout area in portrait mode if not in fullscreen mode. This can cause "jumping" if the
     * user swipes a system bar to show it. It is recommended to set [WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER],
     * call [showBelowCutout] from [Activity.onCreate]
     * (see [Android Developers article about cutouts](https://developer.android.com/guide/topics/display-cutout#never_render_content_in_the_display_cutout_area)).
     * @see showSystemUI
     * @see addSystemUIVisibilityListener
     */
    fun Activity.hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                // and show it again upon user's touch. We just want the user to be able to show the
                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // make navigation bar translucent (alternative to deprecated
                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                // - do this already in hideSystemUI() so that the bar
                // is translucent if user swipes it up
                window.navigationBarColor = getColor(android.R.color.transparent)
                // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and SYSTEM_UI_FLAG_FULLSCREEN.
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    // Do not let system steal touches for showing the navigation bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Hide the nav bar and status bar
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Keep the app content behind the bars even if user swipes them up
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // make navbar translucent - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    /**
     * Shows the system bars and returns back from fullscreen.
     * @see hideSystemUI
     * @see addSystemUIVisibilityListener
     */
    fun Activity.showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // show app content in fullscreen, i. e. behind the bars when they are shown (alternative to
            // deprecated View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.setDecorFitsSystemWindows(false)
            // finally, show the system bars
            window.insetsController?.show(WindowInsets.Type.systemBars())
        } else {
            // Shows the system bars by removing all the flags
            // except for the ones that make the content appear under the system bars.
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    /**
     * Should be called from [Activity.onCreate].
     * @see hideSystemUI
     * @see showSystemUI
     */
    fun Activity.showBelowCutout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

}