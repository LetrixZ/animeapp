package com.letrix.anime.ui.genre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letrix.anime.data.Anime
import com.letrix.anime.network.ApiRepository
import com.letrix.anime.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {
    private val _genre = MutableLiveData<Resource<Anime.List>>()
    val genre: LiveData<Resource<Anime.List>> get() = _genre

    init {
        _genre.postValue(Resource.loading(null))
    }

    fun genre(id: String) {
        viewModelScope.launch {
            _genre.value = try {
                Resource.success(data = apiRepository.getGenre(id))
            } catch (e: Exception) {
                Resource.error(data = null, message = e.message ?: "Unknown error")
            }
        }
    }
}