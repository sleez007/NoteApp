package com.example.noteapp.holder

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balysv.materialripple.MaterialRippleLayout
import com.example.noteapp.R
import com.example.noteapp.activity.NoteActivity
import com.example.noteapp.model.NoteInfo

class NotesHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val noteTitle:TextView = itemView.findViewById<TextView>(R.id.note_title);
    private val noteText:TextView = itemView.findViewById<TextView>(R.id.note_text);
    private val ripple_wrapper = itemView.findViewById<MaterialRippleLayout>(R.id.ripple_wrap)

    private var currentNote: NoteInfo? = null

    fun updateView(note: NoteInfo, courseTitle: String){
        currentNote = note
       // noteTitle.text=note.toString()
        noteTitle.text=courseTitle
        noteText.text=note.mTitle
        ripple_wrapper.setOnClickListener {

            val intent:Intent =Intent(itemView.context,NoteActivity::class.java)
           // intent.putExtra(NoteActivity.NOTE_POSITION,currentNote)
            intent.putExtra(NoteActivity.NOTE_ID,currentNote?._ID)

            itemView.context.startActivity(intent)
        }


    }
}