package com.example.noteapp.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.noteapp.NoteReminderNotification
import com.example.noteapp.R
import com.example.noteapp.broadcasts.NoteReminderReceiver
import com.example.noteapp.contentproviders.NoteKeeperProviderContract.Companion.Notes
import com.example.noteapp.contentproviders.NoteKeeperProviderContract.Companion.Courses
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.CourseInfoEntry
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.dataManager.DataManager
import com.example.noteapp.dataManager.NoteActivityViewModel
import com.example.noteapp.dataManager.NoteKeeperOpenHelper
import com.example.noteapp.helper.CourseEventBroadcastHelper
import com.example.noteapp.model.CourseInfo
import com.example.noteapp.model.NoteInfo
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var mNote: NoteInfo? = null
    var mIsNewNote: Boolean = true
    var courses: MutableList<CourseInfo>? = null
    var mNoteId= 0
    val ID_NOT_SET = -1
    var notePosition: Int = 0
    var mIsCancelling = false
    private var mViewModel: NoteActivityViewModel? = null
    lateinit var adapterCourses: SimpleCursorAdapter


    lateinit var dbOpenHelper: NoteKeeperOpenHelper
    var noteCursor: Cursor? = null
    var mCourseIdPos: Int? = 0
    var mNoteTextPos: Int? = 0
    var mNoteTitlePos: Int? = 0

    var courseQueryFinished: Boolean = false
    var noteQueryFinished: Boolean = false

    var mUri: Uri? = null

    //THIS COULD BE ANY INTEGER
    var LOADER_NOTE = 0
    var LOADER_COURSE = 1

    //ORIGINAL NOTE BACKUP REFERENCE
//    var mOriginalNoteCourseId: String? = null
//    var mOriginalNoteTitle: String? = null
//    var mOriginalNoteText: String? = null

    companion object {
        val NOTE_ID = "com.example.noteapp.activity.NOTE_POSITION"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("tester", position.toString())
        parent?.getItemAtPosition(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Note"

        dbOpenHelper = NoteKeeperOpenHelper(this)

        //THIS CODE IS BASICALLY A BOILER PLATE CODE
        var viewModelProvider: ViewModelProvider = ViewModelProvider(
            viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )

        mViewModel = viewModelProvider.get(NoteActivityViewModel::class.java)

        if (mViewModel!!.mIsNewlyCreated && savedInstanceState != null) {
            mViewModel?.restoreState(savedInstanceState)
        } else {
            mViewModel?.mIsNewlyCreated = false
        }

        adapterCourses = SimpleCursorAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, null,
            arrayOf(CourseInfoEntry.COLUMN_COURSE_TITLE), intArrayOf(android.R.id.text1), 0
        ).also {
            spinner_courses.adapter = it
        }

        supportLoaderManager.initLoader(LOADER_COURSE, null, loaderCallbacks)

        readDisplayStateValues()
        saveOriginalNoteValues()

        if (!mIsNewNote) {
            supportLoaderManager.initLoader(LOADER_NOTE, null, loaderCallbacks)
        }

    }

    val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): CursorLoader {
            var loader: CursorLoader? = null
            loader = when (id) {
                LOADER_NOTE -> {
                    createLoaderNote()
                }
                LOADER_COURSE -> {
                    createLoaderCourse()
                }
                else -> {
                    null
                }
            }
            return loader!!
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            when (loader.id) {
                LOADER_NOTE -> {
                    loadFinishedNotes(data)

                }
                LOADER_COURSE -> {
                    adapterCourses.changeCursor(data)
                    courseQueryFinished = true
                    displayNoteWhenQueryIsFinished()
                }
            }

        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            when (loader.id) {
                LOADER_NOTE -> {
                    if (noteCursor != null) {
                        noteCursor?.close()
                    }
                }

                LOADER_COURSE -> {
                    adapterCourses.changeCursor(null)
                }
            }

        }

    }

    private fun loadFinishedNotes(data: Cursor?) {
        noteCursor = data
        mCourseIdPos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
        mNoteTitlePos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
        mNoteTextPos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)

        //use noteCursor.getCount method to know the number of rows found
        noteCursor?.moveToNext()
        noteQueryFinished = true
        displayNoteWhenQueryIsFinished()
    }

    fun displayNoteWhenQueryIsFinished() {
        if (courseQueryFinished && noteQueryFinished) displayNote()
    }

    fun createLoaderCourse(): CursorLoader {
        courseQueryFinished = false

        //UPDATING THIS FUNCTION TO USE OUR CONTENT PROVIDER
        //Note we use the content:// before our authority uri to specify that we are pointing to a content provider
        val uri: Uri = Courses.CONTENT_URI
        val courseColumns =
            arrayOf(Courses.COLUMN_COURSE_TITLE, Courses.COLUMN_COURSE_ID, Courses._ID)
        return CursorLoader(this, uri, courseColumns, null, null, Courses.COLUMN_COURSE_TITLE)
    }


    fun createLoaderNote(): CursorLoader {
        noteQueryFinished = false

        val noteColumns = arrayOf(
            Notes.COLUMN_COURSE_ID,
            Notes.COLUMN_NOTE_TITLE,
            Notes.COLUMN_NOTE_TEXT
        )

      mUri =  ContentUris.withAppendedId(Notes.CONTENT_URI, mNoteId.toLong())

        return CursorLoader(this,mUri!!,noteColumns,null,null,null)

    }


    override fun onDestroy() {
        dbOpenHelper.close()
        super.onDestroy()
    }

    private fun saveOriginalNoteValues() {
        if (mIsNewNote) return

        mViewModel?.mOriginalNoteCourseId = mNote?.mCourse?.mCourseId
        mViewModel?.mOriginalNoteTitle = mNote?.mTitle
        mViewModel?.mOriginalNoteText = mNote?.mText

    }

    fun displayNote() {
        val courseId = noteCursor?.getString(mCourseIdPos!!)
        val noteTitle = noteCursor?.getString(mNoteTitlePos!!)
        val noteText = noteCursor?.getString(mNoteTextPos!!)

        noteCursor?.close()
        // val course : CourseInfo? = DataManager?.getInstance()?.getCourse(courseId!!)
        val courseIndex: Int = getIndexOfCourseId(courseId)
        spinner_courses.setSelection(courseIndex)
        text_note_title.setText(noteTitle)
        text_note_text.setText(noteText)

        CourseEventBroadcastHelper.sendEventBroadCast(this,courseId?:"","editing note")

    }

    private fun getIndexOfCourseId(courseId: String?): Int {
        val cursor: Cursor = adapterCourses.cursor
        val courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        var courseRowIndex = 0

        var more: Boolean = cursor.moveToFirst()
        while (more) {
            val cursorCourseId: String = cursor.getString(courseIdPos)
            if (courseId.equals(cursorCourseId)) break
            courseRowIndex++
            more = cursor.moveToNext()
        }

        return courseRowIndex
    }

    fun
            readDisplayStateValues() {
//        val intent:Intent = intent
//        mNote= intent.getParcelableExtra(NOTE_POSITION)
//
//        mIsNewNote = (mNote == null)

        val intent: Intent = intent
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET)
        Toast.makeText(this, mNoteId.toString(), Toast.LENGTH_LONG).show()

        mIsNewNote = (mNoteId == ID_NOT_SET)
        if (mIsNewNote) {
            createNewNote()
        }
    }

    private fun createNewNote() {

        val task : AsyncTask<ContentValues, Void, Uri?> = object : AsyncTask<ContentValues, Void, Uri?>(){
            override fun doInBackground(vararg params: ContentValues?): Uri? {
              return   contentResolver.insert(Notes.CONTENT_URI, params[0])
            }

            override fun onPostExecute(result: Uri?) {
                mUri = result
              // super.onPostExecute(result)
            }

        }


        val values: ContentValues = ContentValues()
        values.put(Notes.COLUMN_COURSE_ID, "")
        values.put(Notes.COLUMN_NOTE_TITLE, "")
        values.put(Notes.COLUMN_NOTE_TEXT, "")
        task.execute(values)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_send_gmail -> {
                sendEmail()
            }
            R.id.action_cancel -> {
                mIsCancelling = true
                finish()
            }
            R.id.action_next -> {
                moveNext()
            }
            R.id.action_reminder->{
                showReminderNotification()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showReminderNotification() {
        val noteText = text_note_text.text.toString()
        val noteTitle = text_note_title.text.toString()
        //retrieve id from a uri
       val id =  ContentUris.parseId(mUri).toInt()

        val intent : Intent = Intent(this,NoteReminderReceiver::class.java)
        intent.putExtra(NoteReminderReceiver.EXTRA_NOTE_TITLE,noteTitle)
        intent.putExtra(NoteReminderReceiver.EXTRA_NOTE_TEXT,noteText)
        intent.putExtra(NoteReminderReceiver.EXTRA_NOTE_ID,id)


        val pendingIntent:PendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager:AlarmManager =getSystemService(ALARM_SERVICE) as AlarmManager
         val currentTimeInMilliseconds: Long = SystemClock.elapsedRealtime()
          val  ONE_HOUR = 60*60 *1000
        val alarmTime: Long = currentTimeInMilliseconds + ONE_HOUR
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,alarmTime, pendingIntent)


      // NoteReminderNotification.notify(this,noteText,noteTitle,id)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item: MenuItem? = menu?.findItem(R.id.action_next)
        val lastNoteIndex: Int? = DataManager.getInstance()?.mNotes!!.size - 1
        item?.setEnabled(mNoteId < lastNoteIndex!!)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun moveNext() {
        saveNote()
        ++mNoteId
        mNote = DataManager.getInstance()?.getNotes()?.get(mNoteId)
        saveOriginalNoteValues()
        displayNote()
        invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        if (!mIsCancelling) {
            saveNote()
        } else {
            Toast.makeText(this, notePosition.toString(), Toast.LENGTH_SHORT).show()
            if (mIsNewNote) {
                deleteNoteFromDatabase()
            } else {
                storePreviousNoteValues()
            }

        }

    }


    //DELETE A ROW FROM THE DATABASE TABLE
    private fun deleteNoteFromDatabase() {
        val where = NoteInfoEntry._ID + " = ?"
        val whereArgs = arrayOf(mNoteId.toString())

        val task = object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                val db: SQLiteDatabase = dbOpenHelper.writableDatabase
                db.delete(NoteInfoEntry.TABLE_NAME, where, whereArgs)
                return null
            }

        }

        task.execute()


    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mViewModel?.saveState(outState)
        }
    }

    private fun storePreviousNoteValues() {
        val course: CourseInfo? =
            DataManager.getInstance()?.getCourse(mViewModel?.mOriginalNoteCourseId?:"ffdtf655758b")
        mNote?.mCourse = course
        mNote?.mTitle = mViewModel?.mOriginalNoteTitle
        mNote?.mText = mViewModel?.mOriginalNoteText

    }


    private fun saveNote() {
        val courseId = selectedCourseId()

        val noteTitle = text_note_title.text.toString()

        val noteText = text_note_text.text.toString()

        saveNoteToDatabase(courseId, noteTitle, noteText)
    }

    private fun selectedCourseId(): String {
        val selectedPosition: Int = spinner_courses.selectedItemPosition
        val cursor: Cursor = adapterCourses.cursor
        cursor.moveToPosition(selectedPosition)
        val courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        val courseId = cursor.getString(courseIdPos)
        return courseId

    }


    //THIS METHOD HANDLES NOTE UPDATE
    fun saveNoteToDatabase(courseId: String, noteTitle: String, noteText: String) {

        val selection: String = NoteInfoEntry._ID + " = ?"
        val selectionArgs = arrayOf(mNoteId.toString())

        val values: ContentValues = ContentValues()
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId)
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle)
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText)

        val db: SQLiteDatabase = dbOpenHelper.writableDatabase
        db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs)

    }

    private fun sendEmail() {
        val course: CourseInfo? = spinner_courses.selectedItem as? CourseInfo
        val subject: String = text_note_title.text.toString()
        val text: String =
            "Check ou what i learned in the pluralsight course \"${course?.mTitle}\"\n" +
                    " ${text_note_text.text.toString()}"

        val intent: Intent = Intent(Intent.ACTION_SEND).also {
            it.type = "message/rfc2822"
            it.putExtra(Intent.EXTRA_SUBJECT, subject)
            it.putExtra(Intent.EXTRA_TEXT, text)
        }

        startActivity(intent)
    }
}
