package com.letrix.anime.data

import com.google.gson.annotations.SerializedName

class Jikan {

    data class Anime(
        @SerializedName("mal_id") val malId: Int,
        @SerializedName("image_url") val poster: String,
        @SerializedName("trailer_url") val trailer: String?,
    )

    data class Search(
        val result: List<Anime>
    )

}