package com.letrix.anime.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.letrix.anime.network.JkRepository
import com.letrix.anime.network.miranime.MiranimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: JkRepository,
    private val miranimeRepository: MiranimeRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val currentQuery = state.getLiveData<String>("current_query")

    val results = currentQuery.switchMap { queryString ->
        miranimeRepository.search(queryString).cachedIn(viewModelScope)
    }

    fun search(query: String) {
        currentQuery.value = query
    }

}