package com.letrix.anime.network.miranime

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.letrix.anime.data.Anime
import com.letrix.anime.network.paging.GenreDataSource
import com.letrix.anime.network.paging.SearchDataSource
import com.letrix.anime.network.paging.YearDataSource
import javax.inject.Inject

class MiranimeRepository
@Inject constructor(private val service: MiranimeService) {

    suspend fun getHome(): List<Anime.List> = service.getHome()
    suspend fun getInfo(id: String): Anime = service.getInfo(id)
    suspend fun getServers(id: String, episode: Int) = service.getServers(id, episode)
    suspend fun genreList() = service.genreList()
    suspend fun yearList() = service.yearList()

    fun getYear(year: Int) = Pager(
        config = PagingConfig(
            pageSize = 24,
            maxSize = 24 * 4,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { YearDataSource(year, service) }
    ).liveData

    fun getGenre(genre: String) = Pager(
        config = PagingConfig(
            pageSize = 24,
            maxSize = 24 * 4,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenreDataSource(genre, service) }
    ).liveData

    fun search(query: String) = Pager(
        config = PagingConfig(
            pageSize = 24,
            maxSize = 24 * 4,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchDataSource(query, service) }
    ).liveData

}