package com.letrix.anime.ui.info

import android.os.Bundle
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
class InfoViewModel @Inject constructor(
    private val apiRepository: ApiRepository
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
}