package com.letrix.anime.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.liveData
import com.letrix.anime.data.Anime
import com.letrix.anime.network.paging.GenreDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JkRepository @Inject constructor(
    private val apiService: JkService
) {

    suspend fun getInfo(id: String) = apiService.getInfo(id)

    suspend fun getServers(id: String, episode: Int) = apiService.getServers(id, episode)

    /*fun getGenre(genre: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = MAX_SIZE_UNBOUNDED,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenreDataSource(genre, apiService) }
    ).liveData*/

    /*fun searchAnime(query: String) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 60,
            initialLoadSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchDataSource(query, apiService) }
    ).liveData*/

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