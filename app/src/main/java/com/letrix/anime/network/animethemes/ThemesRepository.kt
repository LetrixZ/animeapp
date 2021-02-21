package com.letrix.anime.network.animethemes

import androidx.lifecycle.LiveData
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ThemesRepository
@Inject constructor(
    private val service: ThemesService,
) {

    suspend fun getThemes(id: Int) = service.getThemes(id)
    suspend fun getThemes(title: String) = service.getThemes(title)

}