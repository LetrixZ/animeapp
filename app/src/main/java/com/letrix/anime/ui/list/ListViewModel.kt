package com.letrix.anime.ui.list

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.letrix.anime.network.miranime.MiranimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val apiRepository: MiranimeRepository, state: SavedStateHandle) : ViewModel() {
    private val currentGenre = state.getLiveData<String>("current_query")
    private val currentYear = state.getLiveData<Int>("current_year")

    val resultsGenre = currentGenre.switchMap { genreString ->
        apiRepository.getGenre(genreString).cachedIn(viewModelScope)
    }

    fun genre(query: String) {
        currentGenre.value = query
    }

    val genreList = liveData { emit(apiRepository.genreList()) }

    val resultsYear = currentYear.switchMap { yearInt ->
        apiRepository.getYear(yearInt).cachedIn(viewModelScope)
    }

    fun year(year: Int) {
        currentYear.value = year
    }

    val yearList = liveData { emit(apiRepository.yearList()) }
}