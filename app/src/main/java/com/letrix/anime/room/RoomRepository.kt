package com.letrix.anime.room

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val dao: AnimeDao
) {

    fun all() = dao.all()
    suspend fun get(showId: String) = dao.get(showId)
    suspend fun getAnime(showId: String) = dao.getAnime(showId)
    suspend fun getEpisode(episodeId: String) = dao.getEpisode(episodeId)

    suspend fun insert(anime: Anime) = dao.insert(anime)
    suspend fun insert(episode: Anime.Episode) = dao.insert(episode)

    suspend fun setCompleted(id: String, completed: Boolean) = dao.setCompleted(id, completed)

    suspend fun upsert(anime: Anime): Boolean {
        val item = dao.getAnime(anime.id)
        return if (item == null) {
            dao.insert(anime)
            true
        } else {
            dao.update(anime)
            false
        }
    }

    suspend fun upsert(episode: Anime.Episode): Boolean {
        val item = dao.getEpisode(episode.id)
        Timber.d(item.toString())
        return if (item == null) {
            dao.insert(episode)
            true
        } else {
            dao.update(episode)
            false
        }
    }

    suspend fun update(anime: Anime): Int = dao.update(anime)

}