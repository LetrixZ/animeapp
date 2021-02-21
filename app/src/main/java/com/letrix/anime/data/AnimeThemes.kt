package com.letrix.anime.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AnimeThemes(
    @SerializedName("mal_id") val malId: Int,
    val title: String,
    val themes: List<Theme>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Theme)!!
    ) {
    }

    data class Theme(
        val slug: String,
        val title: String,
        val type: String,
        val sequence: Int?,
        val group: String,
        val entries: List<Entry>
    ) : Parcelable {

        var selectedVersion = 0
        var selectedVideo = 0

        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()!!,
            parcel.createTypedArrayList(Entry)!!
        ) {
            selectedVersion = parcel.readInt()
            selectedVideo = parcel.readInt()
        }

        data class Entry(
            val slug: String,
            val version: Int?,
            val episodes: String,
            val nsfw: Boolean,
            val spoiler: Boolean,
            val videos: List<Video>
        ) : Parcelable {
            constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString()!!,
                parcel.readByte() != 0.toByte(),
                parcel.readByte() != 0.toByte(),
                parcel.createTypedArrayList(Video)!!
            ) {
            }

            data class Video(
                val filename: String,
                val resolution: Int,
                val nc: Boolean,
                val subbed: Boolean,
                val lyrics: Boolean,
                val uncen: Boolean,
                val source: String,
                val over: String,
                val link: String
            ) : Parcelable {
                constructor(parcel: Parcel) : this(
                    parcel.readString()!!,
                    parcel.readInt(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readString()!!,
                    parcel.readString()!!,
                    parcel.readString()!!
                ) {
                }

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                    parcel.writeString(filename)
                    parcel.writeInt(resolution)
                    parcel.writeByte(if (nc) 1 else 0)
                    parcel.writeByte(if (subbed) 1 else 0)
                    parcel.writeByte(if (lyrics) 1 else 0)
                    parcel.writeByte(if (uncen) 1 else 0)
                    parcel.writeString(source)
                    parcel.writeString(over)
                    parcel.writeString(link)
                }

                override fun describeContents(): Int {
                    return 0
                }

                companion object CREATOR : Parcelable.Creator<Video> {
                    override fun createFromParcel(parcel: Parcel): Video {
                        return Video(parcel)
                    }

                    override fun newArray(size: Int): Array<Video?> {
                        return arrayOfNulls(size)
                    }
                }

            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(slug)
                parcel.writeValue(version)
                parcel.writeString(episodes)
                parcel.writeByte(if (nsfw) 1 else 0)
                parcel.writeByte(if (spoiler) 1 else 0)
                parcel.writeTypedList(videos)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<Entry> {
                override fun createFromParcel(parcel: Parcel): Entry {
                    return Entry(parcel)
                }

                override fun newArray(size: Int): Array<Entry?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(slug)
            parcel.writeString(title)
            parcel.writeString(type)
            parcel.writeValue(sequence)
            parcel.writeString(group)
            parcel.writeTypedList(entries)
            parcel.writeInt(selectedVersion)
            parcel.writeInt(selectedVideo)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Theme> {
            override fun createFromParcel(parcel: Parcel): Theme {
                return Theme(parcel)
            }

            override fun newArray(size: Int): Array<Theme?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(malId)
        parcel.writeString(title)
        parcel.writeTypedList(themes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnimeThemes> {
        override fun createFromParcel(parcel: Parcel): AnimeThemes {
            return AnimeThemes(parcel)
        }

        override fun newArray(size: Int): Array<AnimeThemes?> {
            return arrayOfNulls(size)
        }
    }
}
