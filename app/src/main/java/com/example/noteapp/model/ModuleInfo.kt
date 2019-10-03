package com.example.noteapp.model

import android.os.Parcel
import android.os.Parcelable

data class ModuleInfo(var mModuleId: String? = null ,var mTitle: String? = null, var mIsComplete:Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString(),parcel.readString(),parcel.readByte() == (1 .toByte()))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mModuleId)
        parcel.writeString(mTitle)
        parcel.writeByte(if(mIsComplete)  1.toByte() else 0.toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModuleInfo> {
        override fun createFromParcel(parcel: Parcel): ModuleInfo {
            return ModuleInfo(parcel)
        }

        override fun newArray(size: Int): Array<ModuleInfo?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return mTitle as String
    }

    override fun equals(other: Any?): Boolean {
        if(this == other) return true
        if(other == null || ModuleInfo::class == other::class!!)return false

        val that : ModuleInfo = other as ModuleInfo
        return mModuleId.equals(that.mModuleId)

    }

    override fun hashCode(): Int {
        return  mModuleId.hashCode()
    }


}