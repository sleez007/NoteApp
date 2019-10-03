package com.example.noteapp.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.noteapp.NoteReminderNotification

class NoteReminderReceiver : BroadcastReceiver() {

    companion object{
        val EXTRA_NOTE_TITLE = "com.example.noteapp.extra.NOTE_TITLE"
        val EXTRA_NOTE_TEXT = "com.example.noteapp.extra.NOTE_TEXT"
        val EXTRA_NOTE_ID = "com.example.noteapp.extra.NOTE_ID"
    }

    override fun onReceive(context: Context, intent: Intent) {
      val noteTitle: String = intent.getStringExtra(EXTRA_NOTE_TITLE)
        val noteText: String = intent.getStringExtra(EXTRA_NOTE_TEXT)
        val noteId :Int = intent.getIntExtra(EXTRA_NOTE_ID,0)

        NoteReminderNotification.notify(context,noteTitle,noteText,noteId)
    }
}
