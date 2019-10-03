package com.example.noteapp.model

import android.os.Parcel
import android.os.Parcelable

data class NoteInfo(var mCourse: CourseInfo?, var mTitle: String?, var mText: String?, var _ID: Int=0): Parcelable {

   private  constructor(parcel: Parcel) : this(
        parcel.readParcelable(CourseInfo::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    private fun getCompareKey(): String {
        return mCourse?.mCourseId + "|" + mTitle + "|" + mText
    }

    override fun toString(): String {
        return getCompareKey()
    }

    override fun hashCode():Int {
        return getCompareKey().hashCode();
    }

    override fun equals(other: Any?): Boolean {
        if( this == other) return true
        if(other == null || NoteInfo::class == other::class!!)return false

        val that : NoteInfo = other as NoteInfo
        return getCompareKey().equals(that.getCompareKey())
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(mCourse,0)
        dest?.writeString(mTitle)
        dest?.writeString(mText)
        dest?.writeInt(_ID)

    }

    override fun describeContents(): Int =0

    companion object CREATOR : Parcelable.Creator<NoteInfo> {
        override fun createFromParcel(parcel: Parcel): NoteInfo {
            return NoteInfo(parcel)
        }

        override fun newArray(size: Int): Array<NoteInfo?> {
            return arrayOfNulls(size)
        }
    }




}