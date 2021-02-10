package com.letrix.anime.network

import com.letrix.anime.data.Anime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getInfo(id: String) = apiService.getInfo(id)

    suspend fun getServers(id: String, episode: Int) = apiService.getServers(id, episode)

    suspend fun getGenre(genre: String) = apiService.getGenre(genre)

    suspend fun getHome(): List<Anime.List> {
        return apiService.getHomeList()
    }

    private fun delaySimulation() {
        try {
            Thread.sleep(DELAY_MS)
        } catch (ignored: InterruptedException) {
        }
    }

    companion object {
        const val DELAY_MS = 2000L
    }

}