package com.letrix.anime.utils

import android.content.Context
import android.util.Base64
import com.letrix.anime.R
import java.nio.charset.StandardCharsets

object Util {

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
        return when (item) {
            "720p", "default", "hd" -> return "Calidad alta (720p)"
            "480p", "sd" -> return "Calidad media (480p)"
            "low" -> return "Calidad baja (360p)"
            "lowest" -> return "Calidad muy baja (240p)"
            "mobile" -> return "No te molestes (144p)"
            else -> "Default"
        }
    }

}