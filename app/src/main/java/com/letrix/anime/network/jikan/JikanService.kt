package com.letrix.anime.network.jikan

import com.letrix.anime.data.Jikan
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanService {

    @GET("/v3/search/anime")
    suspend fun search(@Query("q") query: String): List<Jikan.Anime>

    @GET("/v3/anime/{id}")
    suspend fun get(@Path("id") malId: Int): Jikan.Anime

}