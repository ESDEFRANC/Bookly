package com.altemir.adria.bookly.Model

import android.os.Parcel
import android.os.Parcelable

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Book(val isbn:String, val autor: String, val editorial: String, val title:String, val stars: Double, val uid:String, val uidShelf:String, val uidDrawer:String,val uidUser : String) : Parcelable{
    constructor():this("","","","",0.0,"","","",""){}
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(isbn)
        parcel.writeString(autor)
        parcel.writeString(editorial)
        parcel.writeString(title)
        parcel.writeDouble(stars)
        parcel.writeString(uid)
        parcel.writeString(uidShelf)
        parcel.writeString(uidDrawer)
        parcel.writeString(uidUser)
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