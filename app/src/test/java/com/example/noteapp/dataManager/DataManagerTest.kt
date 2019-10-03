package com.example.noteapp.dataManager

import com.example.noteapp.model.CourseInfo
import com.example.noteapp.model.NoteInfo
import org.junit.Test

import org.junit.Assert.*

class DataManagerTest {

    @Test
    fun createNewNote()  {
        val dm :DataManager?= DataManager.getInstance()
        val course: CourseInfo? = dm?.getCourse("android_intents")
        val noteTitle: String = "Test note title"
        val noteText: String ="This is the body test of my test note"

        val noteIndex: Int? = dm?.createNewNote()
        val newNote: NoteInfo?  = dm?.getNotes()?.get(noteIndex!!)
        newNote?.mCourse = course
        newNote?.mTitle = noteTitle
        newNote?.mText= noteText

        val compareNote:NoteInfo? = dm?.getNotes()?.get(noteIndex!!)

        assertEquals(compareNote?.mCourse,course)
        assertEquals(compareNote?.mTitle,noteTitle)
        assertEquals(compareNote?.mText,noteText)

    }
    
}