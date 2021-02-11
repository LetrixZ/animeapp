package com.letrix.anime.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.letrix.anime.room.Anime
import com.letrix.anime.room.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber.d
import javax.inject.Inject

@HiltViewModel
class RoomViewModel
@Inject constructor(
    private val repository: RoomRepository
) : ViewModel() {

    val list: LiveData<List<Anime.WithEpisodes>> = repository.all()

    fun upsert(anime: Anime) {
        viewModelScope.launch {
            d("Anime ${anime.id} inserted?: ${repository.upsert(anime)}")
        }
    }

    fun getEpisode(episodeId: String) = liveData { emit(repository.getEpisode(episodeId)) }

    fun upsert(episode: Anime.Episode, anime: Anime) {
        viewModelScope.launch {
            val item = repository.getAnime(anime.id)
            if (item != null) {
                repository.upsert(anime)
                d("Episode ${episode.id} inserted?: ${repository.upsert(episode)}")
            } else {
                d("Anime ${anime.id} not in database.")
                repository.insert(anime)
                repository.insert(episode)
                d("Anime ${anime.id} inserted, and episode ${episode.id} inserted")
            }
            checkCompleted()
        }
    }

    fun checkCompleted() = viewModelScope.launch {
        list.value?.forEach {
            val anime = it.anime
            val episodes = it.episodes.filter { episode -> episode.completed() }
            val completed = !anime.ongoing && episodes.size == anime.totalEpisodes
            d("anime: $anime, episodes: $episodes, completed? $completed, ongoing: ${!anime.ongoing}, ${episodes.size} and ${anime.totalEpisodes}")
            d(repository.setCompleted(anime.id, completed).toString())
        }
    }

}