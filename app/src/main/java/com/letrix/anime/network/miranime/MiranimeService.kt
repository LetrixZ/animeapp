package com.letrix.anime.network.miranime

import com.letrix.anime.data.Anime
import com.letrix.anime.data.Genre
import com.letrix.anime.data.Server
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiranimeService {

    @GET("/api/v1/animeflv/home")
    suspend fun getHome(): List<Anime.List>

    @GET("/api/v1/jkanime/home")
    suspend fun getHomeJK(): List<Anime.List>

    @GET("/api/v1/animeflv/info/{id}")
    suspend fun getInfo(@Path("id") id: String, @Query("extra") extra: Int = 1): Anime

    @GET("/api/v1/animeflv/search")
    suspend fun search(@Query("q") query: String, @Query("page") page: Int): List<Anime>

    @GET("/api/v1/animeflv/servers/{id}")
    suspend fun getServers(@Path("id") id: String, @Query("episode") episode: Int): List<Server>

    @GET("/api/v1/animeflv/genre")
    suspend fun getGenre(@Query("genre") genre: String, @Query("page") page: Int = 1, @Query("lite") lite: Int = 1): Anime.List

    @GET("/api/v1/animeflv/genre")
    suspend fun genreList(): List<Genre>

    @GET("/api/v1/animeflv/year")
    suspend fun getYear(@Query("year") yeat: Int, @Query("page") page: Int = 1, @Query("lite") lite: Int = 1): Anime.List

    @GET("/api/v1/animeflv/year")
    suspend fun yearList(): List<String>

}