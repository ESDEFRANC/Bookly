package com.altemir.adria.bookly.Model

import android.os.Parcel
import android.os.Parcelable

class Drawer(val uid:String,val uidUser : String, val name :String) :Parcelable {
    constructor():this("","",""){}
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(uidUser)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Drawer> {
        override fun createFromParcel(parcel: Parcel): Drawer {
            return Drawer(parcel)
        }

        override fun newArray(size: Int): Array<Drawer?> {
            return arrayOfNulls(size)
        }
    }
}
