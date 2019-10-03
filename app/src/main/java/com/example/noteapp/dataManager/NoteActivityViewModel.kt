package com.example.noteapp.dataManager

import android.os.Bundle
import androidx.lifecycle.ViewModel

class NoteActivityViewModel(var mOriginalNoteCourseId: String?=null,var mOriginalNoteTitle: String? = null,var mOriginalNoteText: String? = null): ViewModel() {
    companion object{
        val ORIGINAL_NOTE_COURSE_ID = "com.example.noteapp.ORIGINAL_NOTE_COURSE_ID"
        val ORIGINAL_NOTE_TITTLE = "com.example.noteapp.ORIGINAL_NOTE_TITTLE"
        val ORIGINAL_NOTE_TEXT= "com.example.noteapp.ORIGINAL_NOTE_TEXT"
    }

    var mIsNewlyCreated: Boolean = true

    fun saveState(outState: Bundle) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId)
        outState.putString(ORIGINAL_NOTE_TITTLE,mOriginalNoteTitle)
        outState.putString(ORIGINAL_NOTE_TEXT,mOriginalNoteText)
    }

    fun restoreState(inState: Bundle){
        mOriginalNoteCourseId = inState.getString(ORIGINAL_NOTE_COURSE_ID)
        mOriginalNoteTitle = inState.getString(ORIGINAL_NOTE_TITTLE)
        mOriginalNoteText =  inState.getString(ORIGINAL_NOTE_TEXT)
    }
}