package com.letrix.anime.ui.info

import android.os.Bundle
import androidx.lifecycle.*
import com.letrix.anime.data.Anime
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.data.Server
import com.letrix.anime.network.animethemes.ThemesRepository
import com.letrix.anime.network.jikan.JikanRepository
import com.letrix.anime.network.miranime.MiranimeRepository
import com.letrix.anime.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val miranimeRepository: MiranimeRepository,
    private val jikanRepository: JikanRepository,
    private val themesRepository: ThemesRepository
) : ViewModel() {

    val bundle = Bundle()

    fun getJikanAnime(malId: Int) = liveData { emit(jikanRepository.getJikanAnime(malId)) }

    suspend fun servers(id: String, episode: Int): Resource<List<Server>> {
        return withContext(IO) {
            try {
                return@withContext Resource.success(miranimeRepository.getServers(id, episode))
            } catch (e: Exception) {
                return@withContext Resource.error(message = e.message ?: "Unknown error")
            }
        }
    }

    fun anime(id: String): LiveData<Anime> {
        return liveData(IO) {
            val anime = miranimeRepository.getInfo(id)
            emit(anime)
        }
    }

    fun themes(id: Int): LiveData<AnimeThemes> {
        return liveData(IO) {
            emit(themesRepository.getThemes(id))
        }
    }

    private val _themes = MutableLiveData<Resource<AnimeThemes?>>(Resource.success(null))

    val themes: LiveData<Resource<AnimeThemes?>> = _themes

    fun fetchThemes(title: String) = viewModelScope.launch {
        try {
            _themes.postValue(Resource.success(data = themesRepository.getThemes(title)))
        } catch (e: Exception) {
            _themes.postValue(Resource.error(e.message.toString()))
        }
    }

    private val _info = MutableLiveData<Resource<Anime?>>(Resource.loading())

    val info: LiveData<Resource<Anime?>> = _info

    fun fetchInfo(id: String) = viewModelScope.launch {
        try {
            _info.postValue(Resource.success(data = miranimeRepository.getInfo(id)))
        } catch (e: Exception) {
            _info.postValue(Resource.error(e.message.toString()))
        }
    }
}