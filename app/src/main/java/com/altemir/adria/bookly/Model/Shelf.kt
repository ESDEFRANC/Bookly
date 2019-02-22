package com.altemir.adria.bookly.Model

import android.os.Parcel
import android.os.Parcelable

class Shelf(val uid:String,val uidDrawer : String, val name : String):Parcelable{
    constructor():this("","",""){}
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(uidDrawer)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Shelf> {
        override fun createFromParcel(parcel: Parcel): Shelf {
            return Shelf(parcel)
        }

        override fun newArray(size: Int): Array<Shelf?> {
            return arrayOfNulls(size)
        }
    }

}