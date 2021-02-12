package com.letrix.anime.ui.info

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letrix.anime.data.Anime
import com.letrix.anime.data.Server
import com.letrix.anime.network.JkRepository
import com.letrix.anime.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val apiRepository: JkRepository
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
                Resource.success(data = apiRepository.getInfo(id))
            } catch (e: Exception) {
                Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        }
    }

    suspend fun servers(id: String, episode: Int): Resource<List<Server>> {
        return withContext(IO) {
            try {
                return@withContext Resource.success(apiRepository.getServers(id, episode))
            } catch (e: Exception) {
                return@withContext Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        }
    }


    /*fun servers(id: String, episode: Int) = liveData {
        emit(
            try {
                Resource.success(data = apiRepository.getServers(id, episode))
            } catch (e: Exception) {
                Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        )
    }*/
}