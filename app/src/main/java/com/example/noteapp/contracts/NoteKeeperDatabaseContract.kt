package com.example.noteapp.contracts

import android.provider.BaseColumns
import android.provider.BaseColumns._ID

class NoteKeeperDatabaseContract {
    companion object{
        class CourseInfoEntry: BaseColumns{
            companion object{
                val TABLE_NAME ="course_info"
                val COLUMN_COURSE_ID = "course_id"
                val COLUMN_COURSE_TITLE="course_title"
                val _ID = BaseColumns._ID
                val SQL_CREATE_TABLE =
                    "CREATE TABLE $TABLE_NAME ( $_ID INTEGER PRIMARY KEY," +
                            "$COLUMN_COURSE_ID TEXT UNIQUE NOT NULL,$COLUMN_COURSE_TITLE TEXT NOT NULL) "

                //CREATE INDEX course_info_index1 on course_info (course_title)

                val INDEX1 : String = TABLE_NAME+"_index1"
                val SQL_CREATE_INDEX1 = "CREATE INDEX $INDEX1 ON $TABLE_NAME ($COLUMN_COURSE_TITLE)"

                fun getQName(columnName: String): String = TABLE_NAME+"."+columnName
            }
        }

        class NoteInfoEntry: BaseColumns{
            companion object{
                val TABLE_NAME = "note_info"
                val COLUMN_COURSE_ID = "course_id"
                val COLUMN_NOTE_TITLE="note_title"
                val COLUMN_NOTE_TEXT="note_text"
                val _ID = BaseColumns._ID

                val SQL_CREATE_TABLE =
                    "CREATE TABLE $TABLE_NAME (" +
                            "$_ID INTEGER PRIMARY KEY, "+
                            "$COLUMN_NOTE_TITLE TEXT NOT NULL," +
                            "$COLUMN_NOTE_TEXT TEXT," +
                            "$COLUMN_COURSE_ID TEXT NOT NULL) "

                val INDEX1 : String = TABLE_NAME+"_index1"
                val SQL_CREATE_INDEX1 = "CREATE INDEX $INDEX1 ON $TABLE_NAME ($COLUMN_NOTE_TITLE)"

                fun getQName(columnName: String): String =TABLE_NAME +"."+columnName

            }
        }
    }
}