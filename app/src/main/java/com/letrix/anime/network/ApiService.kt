package com.letrix.anime.network

import com.letrix.anime.data.Anime
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(RestConfig.JKANIME_HOME)
    suspend fun getHomeList(): List<Anime.List>

    @GET(RestConfig.JKANIME_INFO + "/{id}")
    suspend fun getInfo(@Path("id") id: String): Anime

    @GET(RestConfig.JKANIME_GENRE)
    suspend fun getGenre(@Query("genre") genre: String, @Query("page") page: Int = 1): Anime.List

    @GET(RestConfig.JKANIME_SEARCH)
    suspend fun searchAnime(@Query("q") query: String, @Query("page") page: Int = 1): List<Anime>

}