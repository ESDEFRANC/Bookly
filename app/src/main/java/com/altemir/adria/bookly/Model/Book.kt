package com.altemir.adria.bookly.Model

import android.os.Parcel
import android.os.Parcelable

class Book(val uid:String,val uidShelf:String,val Title:String, val Description : String, val Editorial : String) : Parcelable{
    constructor():this("","","","",""){}
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(Title)
        parcel.writeString(Description)
        parcel.writeString(Editorial)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }

}