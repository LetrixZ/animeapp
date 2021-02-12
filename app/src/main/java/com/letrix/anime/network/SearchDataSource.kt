package com.letrix.anime.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.letrix.anime.data.Anime
import retrofit2.HttpException
import java.io.IOException

class SearchDataSource(private val query: String, private val apiService: JkService) :
    PagingSource<Int, Anime>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        val position = params.key ?: 1
        return try {
            val response = apiService.searchAnime(query, position)
            LoadResult.Page(
                data = response,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
        return state.anchorPosition
    }
}