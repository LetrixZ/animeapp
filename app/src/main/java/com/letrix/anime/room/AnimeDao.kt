package com.letrix.anime.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(anime: Anime): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(episode: Anime.Episode): Long

    @Update(entity = Anime::class)
    suspend fun update(anime: Anime): Int

    @Update(entity = Anime.Episode::class)
    suspend fun update(episode: Anime.Episode): Int

    @Query("SELECT * FROM anime")
    suspend fun allAnime(): List<Anime>

    @Query("SELECT * FROM episode")
    suspend fun allEpisode(): List<Anime.Episode>

    @Query("SELECT * FROM anime")
    fun all(): LiveData<List<Anime.WithEpisodes>>

    @Query("SELECT * FROM anime WHERE id == :id")
    suspend fun get(id: String): Anime.WithEpisodes?

    @Query("SELECT * FROM anime WHERE id == :id")
    suspend fun getAnime(id: String): Anime?

    @Query("SELECT * FROM episode WHERE episode_id == :episodeId")
    suspend fun getEpisode(episodeId: String): Anime.Episode?

    @Query("UPDATE Anime SET completed=:completed WHERE id=:id")
    suspend fun setCompleted(id: String, completed: Boolean): Int

}
