package com.letrix.anime.ui.home

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letrix.anime.data.Anime
import com.letrix.anime.network.miranime.MiranimeRepository
import com.letrix.anime.utils.NetworkHelper
import com.letrix.anime.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val miranimeRepository: MiranimeRepository,
    networkHelper: NetworkHelper
) : ViewModel() {
    var container: SparseArray<Parcelable>? = SparseArray()
    private val _home = MutableLiveData<Resource<List<Anime.List>>>()
    val home: LiveData<Resource<List<Anime.List>>> get() = _home

    val bundle = Bundle()

    init {
        _home.postValue(Resource.loading())

        if (networkHelper.isNetworkConnected()) {
            fetchHome()
        } else {
            _home.postValue(
                Resource.error(
                    message = internetErr
                )
            )
        }
    }

    private fun fetchHome() {
        viewModelScope.launch {
            _home.value = try {
                Resource.success(data = miranimeRepository.getHome())
            } catch (exception: Exception) {
                Resource.error(
                    message = exception.message ?: otherErr
                )
            }
        }
    }

    companion object {
        const val internetErr = "Network is down.\n" +
                "Please check\n" +
                "your INTERNET connection!"
        const val otherErr = "Error Occurred!"
    }
}