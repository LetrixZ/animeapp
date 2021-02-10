package com.letrix.anime.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.letrix.anime.network.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: ApiRepository,
    state: SavedStateHandle
) : ViewModel() {

    val currentQuery = state.getLiveData<String>("current_query")

    val results = currentQuery.switchMap { queryString ->
        repository.searchAnime(queryString).cachedIn(viewModelScope)
    }

    fun search(query: String) {
        currentQuery.value = query
    }

}