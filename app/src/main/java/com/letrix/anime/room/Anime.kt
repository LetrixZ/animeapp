package com.letrix.anime.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.letrix.anime.data.Anime as AnimeData

@Entity
data class Anime(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", index = true)
    val id: String,
    val title: String,
    val poster: String,
    val latestEpisode: Int,
    val totalEpisodes: Int,
    val watchedEpisodes: Int,
    var completed: Boolean,
    var ongoing: Boolean,
) {

    constructor(anime: AnimeData, episode: Int) : this(
        anime.id,
        anime.title,
        anime.poster,
        episode,
        anime.totalEpisodes!!,
        0,
        false,
        anime.ongoing!!
    )

    @Entity(
        foreignKeys = [ForeignKey(
            entity = Anime::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("anime_id"),
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Episode(
        @ColumnInfo(name = "anime_id")
        val animeId: String,
        val episode: Int,
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "episode_id")
        val id: String = "$animeId-$episode",
        val progress: Long,
        val duration: Long
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readLong()
        ) {
        }

        fun completed() = (progress * 100 / duration) > 90
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(animeId)
            parcel.writeInt(episode)
            parcel.writeString(id)
            parcel.writeLong(progress)
            parcel.writeLong(duration)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Episode> {
            override fun createFromParcel(parcel: Parcel): Episode {
                return Episode(parcel)
            }

            override fun newArray(size: Int): Array<Episode?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class WithEpisodes(
        @Embedded val anime: Anime,
        @Relation(
            parentColumn = "id",
            entityColumn = "anime_id"
        )
        val episodes: List<Episode>
    )

}
