package com.example.noteapp.dataManager

import android.database.sqlite.SQLiteDatabase
import com.example.noteapp.contracts.NoteKeeperDatabaseContract
import android.content.ContentValues




class DatabaseDataWorker(var mDb: SQLiteDatabase? = null) {


    fun insertCourses(){
        insertCourse("android_intents","Android programming with intents")
        insertCourse("android_async","Android async programming and services")
        insertCourse("java_lang","Java Fundamentals: The java Language")
        insertCourse("java_core","Java Fundamentals: The core platform ")
    }

    fun insertSimpleNote(){
        insertNote("android_intents", "Dynamic intent resolution", "Wow, intents allow components to be resolved at runtime");
        insertNote("android_intents", "Delegating intents", "PendingIntents are powerful; they delegate much more than just a component invocation");

        insertNote("android_async", "Service default threads", "Did you know that by default an Android Service will tie up the UI thread?");
        insertNote("android_async", "Long running operations", "Foreground Services can be tied to a notification icon");

        insertNote("java_lang", "Parameters", "Leverage variable-length parameter lists?");
        insertNote("java_lang", "Anonymous classes", "Anonymous classes simplify implementing one-use types");

        insertNote("java_core", "Compiler options", "The -jar option isn't compatible with with the -cp option");
        insertNote("java_core", "Serialization", "Remember to include SerialVersionUID to assure version compatibility");
    }

    private fun insertCourse(courseId: String, title: String) {
        val values = ContentValues()
        values.put(NoteKeeperDatabaseContract.Companion.CourseInfoEntry.COLUMN_COURSE_ID, courseId)
        values.put(NoteKeeperDatabaseContract.Companion.CourseInfoEntry.COLUMN_COURSE_TITLE, title)

        val newRowId = mDb?.insert(NoteKeeperDatabaseContract.Companion.CourseInfoEntry.TABLE_NAME, null, values)
    }

    private fun insertNote(courseId: String, title: String, text: String) {
        val values = ContentValues()
        values.put(NoteKeeperDatabaseContract.Companion.NoteInfoEntry.COLUMN_COURSE_ID, courseId)
        values.put(NoteKeeperDatabaseContract.Companion.NoteInfoEntry.COLUMN_NOTE_TITLE, title)
        values.put(NoteKeeperDatabaseContract.Companion.NoteInfoEntry.COLUMN_NOTE_TEXT, text)

        val newRowId = mDb?.insert(NoteKeeperDatabaseContract.Companion.NoteInfoEntry.TABLE_NAME, null, values)
    }

}