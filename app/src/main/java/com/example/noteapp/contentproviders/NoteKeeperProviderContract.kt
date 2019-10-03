package com.example.noteapp.contentproviders

import android.net.Uri
import android.provider.BaseColumns

class  NoteKeeperProviderContract  private constructor(){

    companion object{
        val AUTHORITY : String = "com.example.noteapp.provider"
        val AUTHORITY_URI : Uri = Uri.parse("content://${AUTHORITY}")



        class Courses : BaseColumns{
            companion object{
                val PATH : String = "courses"
                // content://com.example.noteapp.provider/courses
                //NOTE: THE FIRST PARAMETER HERE IS OF TYPE URI WHILE THE SECOND IS OF TYPE STRING
                val CONTENT_URI : Uri = Uri.withAppendedPath(AUTHORITY_URI,PATH)

                val COLUMN_COURSE_ID : String = "course_id"
                val COLUMN_COURSE_TITLE  ="course_title"
                val _ID = BaseColumns._ID
            }
        }

        class Notes: BaseColumns{
            companion object{
                val PATH : String ="notes"
                val CONTENT_URI : Uri = Uri.withAppendedPath(AUTHORITY_URI,PATH)

                //This section wrapped in comment is for table join cases, i could have created another class for it, but lets avoid redundant code
                //WE ARE TRYING TO JOIN NOTES AND COURSES TABLE TOGETHER
                val PATH_EXPANDED ="notes_expanded"
                val CONTENT_EXPANDED_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED)
                val COLUMN_COURSE_TITLE  ="course_title"
                //TABLE JOIN CHILD ENDS HERE

               val COLUMN_NOTE_TITLE: String ="note_title"
               val COLUMN_NOTE_TEXT: String ="note_text"
               val COLUMN_COURSE_ID: String ="course_id"
                val _ID = BaseColumns._ID
            }
        }


    }
}