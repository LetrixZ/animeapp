package com.letrix.anime.ui.home

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letrix.anime.data.Anime
import com.letrix.anime.network.JkRepository
import com.letrix.anime.network.NetworkHelper
import com.letrix.anime.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiRepository: JkRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    var container: SparseArray<Parcelable>? = SparseArray()
    private val _home = MutableLiveData<Resource<List<Anime.List>>>()
    val home: LiveData<Resource<List<Anime.List>>> get() = _home

    val bundle = Bundle()

    init {
        _home.postValue(Resource.loading(null))

        if (networkHelper.isNetworkConnected()) {
            fetchHome()
        } else {
            _home.postValue(
                Resource.error(
                    data = null,
                    message = internetErr
                )
            )
        }
    }

    private fun fetchHome() {
        viewModelScope.launch {
            _home.value = try {
                Resource.success(data = apiRepository.getHome())
            } catch (exception: Exception) {
                Resource.error(
                    data = null,
                    message = exception.message ?: otherErr
                )
            }
        }
    }

    companion object {
        private val TAG = "MYLOG MainVM"
        const val internetErr = "Network is down.\n" +
                "Please check\n" +
                "your INTERNET connection!"
        const val otherErr = "Error Occurred!"

        fun lgd(s: String) = Timber.d(s)
        fun lge(s: String) = Timber.e(s)
        fun lgi(s: String) = Timber.i(s)
    }
}