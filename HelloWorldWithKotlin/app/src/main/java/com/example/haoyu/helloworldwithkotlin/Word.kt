package com.example.haoyu.helloworldwithkotlin

/**
 * Created by HAOYU on 2017/11/8.
 */

import android.os.Parcel
import android.os.Parcelable

class Word : Parcelable {

    var name: String? = null
        private set
    //var isFavorite: Boolean? =null

    constructor(name: String/*, isFavorite: Boolean*/) {
        this.name = name
        //this.isFavorite = isFavorite
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Word) return false

        val word = o as Word?

        //if (isFavorite != word!!.isFavorite) return false
        return if (name != null) name == word!!.name else word!!.name == null

    }

    override fun hashCode(): Int {
        val result = if (name != null) name!!.hashCode() else 0
        //result = 31 * result + if (isFavorite!!) 1 else 0
        return result
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object {

        val CREATOR: Parcelable.Creator<Word> = object : Parcelable.Creator<Word> {
            override fun createFromParcel(`in`: Parcel): Word = Word(`in`)

            override fun newArray(size: Int): Array<Word?> = arrayOfNulls(size)
        }
    }
}
