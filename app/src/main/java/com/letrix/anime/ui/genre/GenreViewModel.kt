package com.letrix.anime.ui.genre

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.letrix.anime.network.JkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(private val apiRepository: JkRepository, state: SavedStateHandle) : ViewModel() {
    private val currentGenre = state.getLiveData<String>("current_query")

    val results = currentGenre.switchMap { genreString ->
        apiRepository.getGenre(genreString).cachedIn(viewModelScope)
    }

    fun genre(query: String) {
        currentGenre.value = query
    }
}