package com.letrix.anime.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Anime(
    val id: String,
    val title: String,
    val synonyms: kotlin.collections.List<String>? = emptyList(),
    @SerializedName("cover") val poster: String,
    @SerializedName("thumbnail") val thumbnail: String? = "",
    val type: String? = "Serie",
    val duration: Int? = 0,
    val genres: kotlin.collections.List<String>? = emptyList(),
    val ongoing: Boolean? = false,
    @SerializedName("total_episodes") val totalEpisodes: Int? = 1,
    val synopsis: String? = "",
    val likes: Int? = 0,
    @SerializedName("episode") val latestEpisode: Int? = 1,
) : Parcelable {

    var flvId: String = ""
    var watched = emptyList<Int>()
    var nextEpisode: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readValue(
            Int::
            class.java.classLoader
        ) as? Int,
        parcel.createStringArrayList(),
        parcel.readValue(
            Boolean::
            class.java.classLoader
        ) as? Boolean,
        parcel.readValue(
            Int::
            class.java.classLoader
        ) as? Int,
        parcel.readString(),
        parcel.readValue(
            Int::
            class.java.classLoader
        ) as? Int,
        parcel.readValue(
            Int::
            class.java.classLoader
        ) as? Int
    ) {
        flvId = parcel.readString()!!
    }

    data class List(
        val title: String,
        @SerializedName("anime") val list: kotlin.collections.List<Anime>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeStringList(synonyms)
        parcel.writeString(poster)
        parcel.writeString(thumbnail)
        parcel.writeString(type)
        parcel.writeValue(duration)
        parcel.writeStringList(genres)
        parcel.writeValue(ongoing)
        parcel.writeValue(totalEpisodes)
        parcel.writeString(synopsis)
        parcel.writeValue(likes)
        parcel.writeValue(latestEpisode)
        parcel.writeString(flvId)
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