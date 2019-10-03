package com.example.noteapp.contentproviders

/**
 * @author : Etoka kingsley
 * @desc : Content provider to abstract all database query implementation. incase you dont know much about content providers, they help you manage
 * how your app data is being exposed outside the app
 * SIMPLY DEFINITION: A content provider is a  standardized way to expose data from your app to itself and other apps (optional)
 * it is also important to note that content provider is not a database or storage mechanism and therefore needs a storage mechanism like sqlite or
 * informatiom from a server tp provide to the client requesting the information
 */

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.provider.BaseColumns
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.CourseInfoEntry
import com.example.noteapp.dataManager.NoteKeeperOpenHelper
import com.example.noteapp.contentproviders.NoteKeeperProviderContract.Companion.Notes

class NoteKeeperProvider : ContentProvider() {
    val MIME_VENDOR_TYPE = "/vnd" + NoteKeeperProviderContract.AUTHORITY

    companion object {
        val COURSES = 0
        val NOTES = 1
        val NOTES_EXPANDED = 2
        val NOTES_ROW = 3
        //The parameter  UriMatcher.No_Match CONSTANT passed in indicates that any attempt to match a URI that doesnt ptovide an authority
        //or a parent should return back the value no match
        val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(
                NoteKeeperProviderContract.AUTHORITY,
                NoteKeeperProviderContract.Companion.Courses.PATH,
                COURSES
            )
            sUriMatcher.addURI(
                NoteKeeperProviderContract.AUTHORITY,
                Notes.PATH,
                NOTES
            )
            sUriMatcher.addURI(
                NoteKeeperProviderContract.AUTHORITY,
                NoteKeeperProviderContract.Companion.Notes.PATH_EXPANDED,
                NOTES_EXPANDED
            )

            //THIS IS FOR ROW MATCHING
            sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, "${Notes.PATH}/#", NOTES_ROW)
        }
    }

    private lateinit var mDbOpenHelper: NoteKeeperOpenHelper


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        var mimeType: String? = null
        val uriMatcher: Int = sUriMatcher.match(uri)
        when (uriMatcher) {
            COURSES -> {
                //vnd.android.cursor.dir/vnd/com.example.noteapp.provider.courses
                mimeType =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_VENDOR_TYPE + "." + NoteKeeperProviderContract.Companion.Courses.PATH
            }

            NOTES -> {
                mimeType =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_VENDOR_TYPE + "." + NoteKeeperProviderContract.Companion.Notes.PATH
            }
            NOTES_EXPANDED -> {
                mimeType =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_VENDOR_TYPE + "." + NoteKeeperProviderContract.Companion.Notes.PATH_EXPANDED
            }

            NOTES_ROW->{
                mimeType =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE+ MIME_VENDOR_TYPE +"." + NoteKeeperProviderContract.Companion.Notes.PATH
            }

        }
        return mimeType
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db: SQLiteDatabase = mDbOpenHelper.writableDatabase
        var rowId: Long = -1
        var rowUri: Uri? = null
        val uriMatch = sUriMatcher.match(uri)
        var table = ""
        when (uriMatch) {
            NOTES -> {
                table = NoteInfoEntry.TABLE_NAME
            }
            COURSES -> {
                table = CourseInfoEntry.TABLE_NAME
            }
            else -> {
                return null
            }
        }

        rowId = db.insert(table, null, values)

        //content://com.jwhh.jim.notekeeper.provider/notes/rowId
        rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId)



        return rowUri

    }

    override fun onCreate(): Boolean {
        mDbOpenHelper = NoteKeeperOpenHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {

        var cursor: Cursor? = null
        val db: SQLiteDatabase = mDbOpenHelper.readableDatabase

        val uriMatch: Int = sUriMatcher.match(uri)

        var table = ""

        when (uriMatch) {
            COURSES -> {
                table = CourseInfoEntry.TABLE_NAME
                cursor =
                    db.query(table, projection, selection, selectionArgs, null, null, sortOrder)
            }
            NOTES -> {
                table = NoteInfoEntry.TABLE_NAME
                cursor =
                    db.query(table, projection, selection, selectionArgs, null, null, sortOrder)
            }
            NOTES_EXPANDED -> {
                table =
                    "${NoteInfoEntry.TABLE_NAME} JOIN ${CourseInfoEntry.TABLE_NAME} ON ${NoteInfoEntry.getQName(
                        NoteInfoEntry.COLUMN_COURSE_ID
                    )} = ${CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID)}"
                val myProj: Array<String?> = arrayOfNulls<String>(projection?.size ?: 0)
                var idx = 0
                while (idx < projection?.size ?: 0) {
                    myProj[idx] = if (projection?.get(idx).equals(BaseColumns._ID)
                        || projection?.get(idx).equals(CourseInfoEntry.COLUMN_COURSE_ID)
                    )
                        NoteInfoEntry.getQName(projection?.get(idx) ?: "") else projection?.get(idx)
                    idx++
                }
                cursor = db.query(table, myProj, selection, selectionArgs, null, null, sortOrder)

            }
            NOTES_ROW -> {
                val rowId: Long = ContentUris.parseId(uri)
                val rowSelection: String = NoteInfoEntry._ID + " =?"
                val rowSelectionArgs = arrayOf(rowId.toString())
                table = NoteInfoEntry.TABLE_NAME
                cursor =
                    db.query(table, projection, rowSelection, rowSelectionArgs, null, null, null)

            }
            else -> {
                return cursor
            }
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}
