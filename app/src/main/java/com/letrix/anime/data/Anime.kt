package com.letrix.anime.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Anime(
    val id: String,
    val title: String,
    val poster: String,
    val synonyms: kotlin.collections.List<String>? = emptyList(),
    val synopsis: String? = "",
    val thumbnail: String? = "",
    val banner: String? = "",
    val type: String? = "Serie",
    val genres: kotlin.collections.List<String>? = emptyList(),
    val state: Int? = 0,
    @SerializedName("total_episodes") val totalEpisodes: Int? = 1,
    val rating: String? = "",
    val votes: Int? = 0,
    @SerializedName("episode") val latestEpisode: Int? = 1,
    @SerializedName("next_episode") val nextEpisodeDate: String? = "",
    val episodes: kotlin.collections.List<Episode>? = emptyList(),
    @SerializedName("mal_id") val malId: Int? = 0
//    @SerializedName("more_info") val jikanInfo: JikanInfo? = null
) : Parcelable {

    var watched = emptyList<Int>()
    var nextEpisode: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(Episode),
        parcel.readInt()
//        parcel.readParcelable(JikanInfo::class.java.classLoader)
    ) {
        nextEpisode = parcel.readInt()
    }

    data class List(
        val title: String,
        val list: kotlin.collections.List<Anime>
    )

    data class Episode(
        val id: String = "",
        val episode: Int = 1,
        val thumbnail: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeInt(episode)
            parcel.writeString(thumbnail)
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

    data class JikanInfo(
        @SerializedName("mal_id") val id: Int,
        val trailer: String?,
        val rating: Float?,
        val votes: Int?,
        val popularity: Int?,
        val rank: Int?,
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(trailer)
            parcel.writeValue(rating)
            parcel.writeValue(votes)
            parcel.writeValue(popularity)
            parcel.writeValue(rank)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<JikanInfo> {
            override fun createFromParcel(parcel: Parcel): JikanInfo {
                return JikanInfo(parcel)
            }

            override fun newArray(size: Int): Array<JikanInfo?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(poster)
        parcel.writeStringList(synonyms)
        parcel.writeString(synopsis)
        parcel.writeString(thumbnail)
        parcel.writeString(banner)
        parcel.writeString(type)
        parcel.writeStringList(genres)
        parcel.writeValue(state)
        parcel.writeValue(totalEpisodes)
        parcel.writeString(rating)
        parcel.writeValue(votes)
        parcel.writeValue(latestEpisode)
        parcel.writeString(nextEpisodeDate)
        parcel.writeTypedList(episodes)
        parcel.writeValue(malId)
//        parcel.writeParcelable(jikanInfo, flags)
        parcel.writeInt(nextEpisode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Anime> {
        override fun createFromParcel(parcel: Parcel): Anime {
            return Anime(parcel)
        }

        override fun newArray(size: Int): Array<Anime?> {
            return arrayOfNulls(size)
        }
    }

}