package com.letrix.anime.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
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

    fun searchAnime(query: String) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 60,
            initialLoadSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchDataSource(query, apiService) }
    ).liveData

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