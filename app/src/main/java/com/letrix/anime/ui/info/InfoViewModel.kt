package com.letrix.anime.ui.info

import android.os.Bundle
import androidx.lifecycle.*
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
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
    private val jikanRepository: JikanRepository
) : ViewModel() {

    val bundle = Bundle()

    private val _info = MutableLiveData<Resource<Anime>>()
    val info: LiveData<Resource<Anime>> get() = _info

    init {
        _info.postValue(Resource.loading(null))
    }

    fun info(id: String) {
        viewModelScope.launch {
            _info.value = try {
                Resource.success(data = miranimeRepository.getInfo(id))
            } catch (e: Exception) {
                Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        }
    }

    fun getJikanAnime(malId: Int) = liveData { emit(jikanRepository.getJikanAnime(malId)) }

    suspend fun servers(id: String, episode: Int): Resource<List<Server>> {
        return withContext(IO) {
            try {
                return@withContext Resource.success(miranimeRepository.getServers(id, episode))
            } catch (e: Exception) {
                return@withContext Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        }
    }

    fun anime(id: String): LiveData<Anime> {
        return liveData(IO) {
            val anime = miranimeRepository.getInfo(id)
            emit(anime)
        }
    }
}