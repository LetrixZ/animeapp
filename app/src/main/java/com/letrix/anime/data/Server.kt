package com.letrix.anime.data

import android.os.Parcel
import android.os.Parcelable

data class Server(
    var name: String,
    val mirrors: List<Mirror>,
    val cookie: String?
) : Parcelable {
    var index: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createTypedArrayList(Mirror)!!,
        parcel.readString()
    ) {
        index = parcel.readInt()
    }

    fun selected(): Mirror = mirrors[index]

    data class Mirror(
        val quality: String,
        val url: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(quality)
            parcel.writeString(url)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Mirror> {
            override fun createFromParcel(parcel: Parcel): Mirror {
                return Mirror(parcel)
            }

            override fun newArray(size: Int): Array<Mirror?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeTypedList(mirrors)
        parcel.writeString(cookie)
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Server> {
        override fun createFromParcel(parcel: Parcel): Server {
            return Server(parcel)
        }

        override fun newArray(size: Int): Array<Server?> {
            return arrayOfNulls(size)
        }
    }

}