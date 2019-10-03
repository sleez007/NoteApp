package com.example.noteapp.dataManager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.CourseInfoEntry

class NoteKeeperOpenHelper(
    context:Context): SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object{
        val DATABASE_NAME = "NoteKeeper.db"
        val DATABASE_VERSION = 2
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(NoteInfoEntry.SQL_CREATE_TABLE)
        db?.execSQL(CourseInfoEntry.SQL_CREATE_TABLE)

        //CREATE THE TABLE INDEXES FOR MORE OPTIMIZED SEARCH FUNCTIONALITIES. THis is optional
        db?.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1)
        db?.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1)


        //The following is not necessary unless you intend to prepopulate the database with info upon creation
        val worker : DatabaseDataWorker = DatabaseDataWorker(db)
        worker.insertCourses()
        worker.insertSimpleNote()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //This method should only be overridden when we have to upgrade the datatbase maybe be due to new indexes added or due to change in database table structure
        if(oldVersion<2){
            db?.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1)
            db?.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1)
        }
    }

}