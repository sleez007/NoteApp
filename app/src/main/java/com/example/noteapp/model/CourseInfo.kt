package com.example.noteapp.model

import android.os.Parcel
import android.os.Parcelable

data class CourseInfo(var mCourseId: String? = null, var mTitle: String? = null,var mModules: MutableList<ModuleInfo>? = null, var _ID: Int=0) : Parcelable  {

    private  constructor(parcel: Parcel) : this(parcel.readString(),parcel.readString(),ArrayList<ModuleInfo>(), parcel.readInt()) {
        parcel.readTypedList(mModules, ModuleInfo)
    }

    fun getModulesCompletionStatus(): BooleanArray {
        val status = BooleanArray(mModules!!.size)

        for (i in mModules!!.indices)
            status[i] = mModules!!.get(i).mIsComplete

        return status
    }

    fun setModulesCompletionStatus(status: BooleanArray) {
        for (i in mModules!!.indices)
            mModules?.get(i)?.mIsComplete=status[i]
    }

    fun getModule(moduleId: String): ModuleInfo? {
        for (moduleInfo in mModules!!) {
            if (moduleId == moduleInfo!!.mModuleId)
                return moduleInfo
        }
        return null
    }

    override fun toString(): String {
        return mTitle as String
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true;
       if (o == null || CourseInfo::class != o::class) return false;

        val that: CourseInfo =  o as CourseInfo;

        return mCourseId.equals(that.mCourseId);
    }

    override fun hashCode(): Int {
       return mCourseId.hashCode()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mCourseId);
        parcel.writeString(mTitle);
        parcel.writeTypedList(mModules);
        parcel.writeInt(_ID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CourseInfo> {
        override fun createFromParcel(parcel: Parcel): CourseInfo {
            return CourseInfo(parcel)
        }

        override fun newArray(size: Int): Array<CourseInfo?> {
            return arrayOfNulls(size)
        }
    }
}