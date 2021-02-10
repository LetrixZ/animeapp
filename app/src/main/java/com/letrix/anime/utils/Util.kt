package com.letrix.anime.utils

import android.content.Context
import com.letrix.anime.R

object Util {

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

}