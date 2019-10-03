package com.example.noteapp.dataManager

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.CourseInfoEntry

import com.example.noteapp.model.ModuleInfo
import com.example.noteapp.model.NoteInfo
import com.example.noteapp.model.CourseInfo


class DataManager private constructor (var mCourses: MutableList<CourseInfo>? = ArrayList<CourseInfo>(), var mNotes: MutableList<NoteInfo>? = ArrayList<NoteInfo>()) {
    companion object{
        var ourInstance: DataManager? = null

        fun getInstance(): DataManager?{
            when(ourInstance){
                null->{
                    ourInstance =
                        DataManager()
                   //ourInstance!!.initializeCourses()
                  // ourInstance!!.initializeExampleNotes()
                }
            }
            return ourInstance
        }

        fun loadFromDatabase(dbHelper : NoteKeeperOpenHelper){
            val db : SQLiteDatabase = dbHelper.readableDatabase
            val courseColumns =  arrayOf(CourseInfoEntry._ID,CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry.COLUMN_COURSE_TITLE)
           val courseCoursor: Cursor = db.query(CourseInfoEntry.TABLE_NAME,
                courseColumns,
                null,null,null,null,"${CourseInfoEntry.COLUMN_COURSE_TITLE} DESC")
            loadCoursesFromDatabase(courseCoursor)

            val notecolumns = arrayOf(
                NoteInfoEntry._ID,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT)

            val noteOrderBy = "${NoteInfoEntry.COLUMN_COURSE_ID},${NoteInfoEntry.COLUMN_NOTE_TITLE}"
            val noteCursor: Cursor = db.query(NoteInfoEntry.TABLE_NAME,
                notecolumns,null,null,null,null,noteOrderBy)

            loadNotesFromDatabase(noteCursor)
        }

        private fun loadNotesFromDatabase(cursor: Cursor) {
            val courseIdPos : Int = cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
            val noteTitlePos : Int = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
            val noteTextPos : Int = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)
            val _idPos : Int = cursor.getColumnIndex(NoteInfoEntry._ID)

            val dm: DataManager = getInstance()!!
            dm.mNotes?.clear()

            while(cursor.moveToNext()){
                val courseId = cursor.getString(courseIdPos)
                val noteTitle  = cursor.getString(noteTitlePos)
                val noteText = cursor.getString(noteTextPos)
                val course : CourseInfo? = dm?.getCourse(courseId)
                val id = cursor.getInt(_idPos)
                val note : NoteInfo = NoteInfo(course,noteTitle,noteText,id)
                dm.mNotes?.add(note)
            }
            cursor.close()
        }

        private fun loadCoursesFromDatabase(cursor: Cursor) {
           val courseIdPos : Int = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
            val courseTitlePos: Int = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE)
            val idPos:Int = cursor.getColumnIndex(CourseInfoEntry._ID)

            val dm: DataManager = getInstance()!!
            dm.mCourses?.clear()
            while (cursor.moveToNext()){
                val course_id = cursor.getString(courseIdPos)
                val course_title = cursor.getString(courseTitlePos)
                val ID = cursor.getInt(idPos)
                val course : CourseInfo = CourseInfo(course_id,course_title,null,ID)
                Log.i("lala",ID.toString())

                dm.mCourses?.add(course)
            }

            cursor.close()
        }
    }


    fun getCurrentUserName(): String {
        return "Etoka Kingsley";
    }

    fun  getCurrentUserEmail():String {
        return "etokakingsley@gmail.com";
    }

    fun getNotes():MutableList<NoteInfo>?  {
        return mNotes;
    }

    fun createNewNote(): Int {

        val note = NoteInfo(null, null, null)

        mNotes?.add(note)

        //THIS RETURNS THE POSITION OF THE NOTE TO BE ADDED
        return mNotes!!.size - 1
    }

    fun findNote(note: NoteInfo): Int {
        for (index in mNotes!!.indices) {
            if (note == mNotes?.get(index))
                return index
        }

        return -1
    }

    fun removeNote(index: Int) {
        mNotes?.removeAt(index)
    }

    fun getCourses(): MutableList<CourseInfo>? {
        return mCourses
    }

    fun getCourse(id: String): CourseInfo? {
        for (course in mCourses!!) {
            if (id == course!!.mCourseId)
                return course
        }
        return null
    }

    fun getNotes(course: CourseInfo): MutableList<NoteInfo> {
        val notes = ArrayList<NoteInfo>()
        for (note in mNotes!!) {
            if (course == note?.mCourse)
                notes.add(note)
        }
        return notes
    }

    fun getNoteCount(course: CourseInfo): Int {
        var count = 0
        for (note in mNotes!!) {
            if (course == note.mCourse)
                count++
        }
        return count
    }

    private fun initializeCourses() {
       mCourses?.add(initializeCourse1())
       mCourses?.add(initializeCourse2())
       mCourses?.add(initializeCourse3())
       mCourses?.add(initializeCourse4())
    }

    fun initializeExampleNotes() {
       val dm : DataManager?  =
           getInstance();
        var course = dm!!.getCourse("android_intents")
        course?.getModule("android_intents_m01")?.mIsComplete = true;
        course?.getModule("android_intents_m02")?.mIsComplete = true;
        course?.getModule("android_intents_m03")?.mIsComplete = true;
        mNotes?.add(
            NoteInfo(
                course!!, "Service default threads",
                "Did you know that by default an Android Service will tie up the UI thread?"
            )
        )
        mNotes?.add(
            NoteInfo(
                course!!, "Long running operations",
                "Foreground Services can be tied to a notification icon"
            )
        )

        course = dm.getCourse("java_lang")
        course?.getModule("java_lang_m01")?.mIsComplete=true
        course?.getModule("java_lang_m02")?.mIsComplete=true
        course?.getModule("java_lang_m03")?.mIsComplete=true
        course?.getModule("java_lang_m04")?.mIsComplete=true
        course?.getModule("java_lang_m05")?.mIsComplete=true
        course?.getModule("java_lang_m06")?.mIsComplete=true
        course?.getModule("java_lang_m07")?.mIsComplete=true

        mNotes?.add(
            NoteInfo(
                course!!, "Parameters",
                "Leverage variable-length parameter lists"
            )
        )

        mNotes?.add(
            NoteInfo(
                course!!, "Anonymous classes",
                "Anonymous classes simplify implementing one-use types"
            )
        )

        course = dm.getCourse("java_core")
        course?.getModule("java_core_m01")?.mIsComplete=true
        course?.getModule("java_core_m02")?.mIsComplete=true
        course?.getModule("java_core_m03")?.mIsComplete=true
        mNotes?.add(
            NoteInfo(
                course!!, "Compiler options",
                "The -jar option isn't compatible with with the -cp option"
            )
        )

       mNotes?.add(
           NoteInfo(
               course!!, "Serialization",
               "Remember to include SerialVersionUID to assure version compatibility"
           )
       )

    }

    private fun initializeCourse1(): CourseInfo {
        val modules = ArrayList<ModuleInfo>()
        modules.add(ModuleInfo("android_intents_m01", "Android Late Binding and Intents"))
        modules.add(ModuleInfo("android_intents_m02", "Component activation with intents"))
        modules.add(
            ModuleInfo(
                "android_intents_m03",
                "Delegation and Callbacks through PendingIntents"
            )
        )
        modules.add(ModuleInfo("android_intents_m04", "IntentFilter data tests"))
        modules.add(
            ModuleInfo(
                "android_intents_m05",
                "Working with Platform Features Through Intents"
            )
        )

        return CourseInfo("android_intents", "Android Programming with Intents", modules)
    }

    private fun initializeCourse2(): CourseInfo {
        val modules = ArrayList<ModuleInfo>()
        modules.add(
            ModuleInfo(
                "android_async_m01",
                "Challenges to a responsive user experience"
            )
        )
        modules.add(
            ModuleInfo(
                "android_async_m02",
                "Implementing long-running operations as a service"
            )
        )
        modules.add(ModuleInfo("android_async_m03", "Service lifecycle management"))
        modules.add(ModuleInfo("android_async_m04", "Interacting with services"))

        return CourseInfo("android_async", "Android Async Programming and Services", modules)
    }

    private fun initializeCourse3(): CourseInfo {
        val modules = ArrayList<ModuleInfo>()
        modules.add(
            ModuleInfo(
                "java_lang_m01",
                "Introduction and Setting up Your Environment"
            )
        )
        modules.add(ModuleInfo("java_lang_m02", "Creating a Simple App"))
        modules.add(ModuleInfo("java_lang_m03", "Variables, Data Types, and Math Operators"))
        modules.add(ModuleInfo("java_lang_m04", "Conditional Logic, Looping, and Arrays"))
        modules.add(ModuleInfo("java_lang_m05", "Representing Complex Types with Classes"))
        modules.add(ModuleInfo("java_lang_m06", "Class Initializers and Constructors"))
        modules.add(ModuleInfo("java_lang_m07", "A Closer Look at Parameters"))
        modules.add(ModuleInfo("java_lang_m08", "Class Inheritance"))
        modules.add(ModuleInfo("java_lang_m09", "More About Data Types"))
        modules.add(ModuleInfo("java_lang_m10", "Exceptions and Error Handling"))
        modules.add(ModuleInfo("java_lang_m11", "Working with Packages"))
        modules.add(
            ModuleInfo(
                "java_lang_m12",
                "Creating Abstract Relationships with Interfaces"
            )
        )
        modules.add(
            ModuleInfo(
                "java_lang_m13",
                "Static Members, Nested Types, and Anonymous Classes"
            )
        )

        return CourseInfo("java_lang", "Java Fundamentals: The Java Language", modules)
    }

    private fun initializeCourse4(): CourseInfo {
        val modules = ArrayList<ModuleInfo>()
        modules.add(ModuleInfo("java_core_m01", "Introduction"))
        modules.add(ModuleInfo("java_core_m02", "Input and Output with Streams and Files"))
        modules.add(ModuleInfo("java_core_m03", "String Formatting and Regular Expressions"))
        modules.add(ModuleInfo("java_core_m04", "Working with Collections"))
        modules.add(ModuleInfo("java_core_m05", "Controlling App Execution and Environment"))
        modules.add(
            ModuleInfo(
                "java_core_m06",
                "Capturing Application Activity with the Java Log System"
            )
        )
        modules.add(ModuleInfo("java_core_m07", "Multithreading and Concurrency"))
        modules.add(ModuleInfo("java_core_m08", "Runtime Type Information and Reflection"))
        modules.add(ModuleInfo("java_core_m09", "Adding Type Metadata with Annotations"))
        modules.add(ModuleInfo("java_core_m10", "Persisting Objects with Serialization"))

        return CourseInfo("java_core", "Java Fundamentals: The Core Platform", modules)
    }
}