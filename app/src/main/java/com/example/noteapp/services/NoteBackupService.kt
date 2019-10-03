package com.example.noteapp.services

import android.app.IntentService
import android.content.Intent
import android.util.Log

const val EXTRA_COURSE_ID = "com.example.noteapp.services.extra.COURSE_ID"

class NoteBackupService : IntentService("NoteBackupService") {

    override fun onHandleIntent(intent: Intent?) {
       val backupCourseId = intent?.getStringExtra(EXTRA_COURSE_ID)
        Log.i("service","i ran in background thread")
        //run anything u want here in background thread
    }

}
