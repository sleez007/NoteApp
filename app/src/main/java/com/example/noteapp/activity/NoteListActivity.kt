package com.example.noteapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.R
import com.example.noteapp.adapters.CourseRecyclerAdapter
import com.example.noteapp.adapters.NotesRecyclerAdapter
import com.example.noteapp.dataManager.DataManager
import com.example.noteapp.model.NoteInfo

import kotlinx.android.synthetic.main.activity_note_list.*
import kotlinx.android.synthetic.main.content_note_list.*

class NoteListActivity : AppCompatActivity() {

    var notes: MutableList<NoteInfo>?= null
    private var adapter : NotesRecyclerAdapter = NotesRecyclerAdapter()

    private var courseAdapter: CourseRecyclerAdapter = CourseRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        setSupportActionBar(toolbar)
        supportActionBar?.title ="Notes Keeper"

        fab.setOnClickListener { view ->
            startActivity(Intent(this,NoteActivity::class.java))
        }
        initializeDisplayContent()
    }

    private fun initializeDisplayContent(){
       notes = DataManager.getInstance()!!.getNotes()

       // recycler_note.layoutManager=LinearLayoutManager(this)
        //adapter.notes =notes
        //recycler_note.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

}
