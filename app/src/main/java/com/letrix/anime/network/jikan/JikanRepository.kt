package com.letrix.anime.network.jikan

import javax.inject.Inject

class JikanRepository
@Inject constructor(private val service: JikanService) {

    suspend fun getJikanAnime(malId: Int) = service.get(malId)
    suspend fun searchJikan(query: String) = service.search(query)

}