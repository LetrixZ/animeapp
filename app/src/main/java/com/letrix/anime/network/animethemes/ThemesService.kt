package com.letrix.anime.network.animethemes

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.letrix.anime.data.AnimeThemes as Anime

interface ThemesService {

    @GET("/api/v1/anime/{id}")
    suspend fun getThemes(@Path("id") malId: Int): Anime

    @GET("/api/v1/match")
    suspend fun getThemes(@Query("q") title: String): Anime

}