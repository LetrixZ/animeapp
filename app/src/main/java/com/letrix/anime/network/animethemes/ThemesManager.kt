package com.letrix.anime.network.animethemes

import androidx.lifecycle.LiveData
import com.letrix.anime.data.AnimeThemes
import com.letrix.anime.network.Resource

class ThemesManager : LiveData<Resource<AnimeThemes?>>() {

    init {
        value = Resource.Success(null)
    }

    /**
     * Set the [AnimeThemes] value and notifies observers.
     */
    internal fun set(themes: AnimeThemes) {
        postValue(Resource.Success(themes))
    }

    /**
     * Clear any information from the device.
     */
    internal fun clear() {
        postValue(Resource.Success(null))
    }

    /**
     * Signals that the resource information is being retrieved from network.
     */
    internal fun loading() {
        postValue(Resource.Loading())
    }

    /**
     * Signals that an error occurred when trying to fetch the resource information.
     */
    internal fun error(t: Throwable) {
        postValue(Resource.Failure(t))
    }

}