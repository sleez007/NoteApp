package com.example.noteapp.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.contracts.NoteKeeperDatabaseContract
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.holder.NotesHolder
import com.example.noteapp.model.NoteInfo

class NotesRecyclerAdapter: RecyclerView.Adapter<NotesHolder>() {
   // var notes:MutableList<NoteInfo>? = ArrayList<NoteInfo>()
     var mCursor : Cursor? = null

    var mCoursePos:Int=0
    var mNoteTitlePos: Int =0
    var mIdPos : Int = 0

        init {
        populateColumnPosition()
    }

     fun populateColumnPosition() {
        if(mCursor == null) return
        //GET COLUMN INDEX FROM MCURSOR
         mCoursePos= mCursor?.getColumnIndex(NoteKeeperDatabaseContract.Companion.CourseInfoEntry.COLUMN_COURSE_TITLE)!!
        mNoteTitlePos= mCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)!!
        mIdPos = mCursor?.getColumnIndex(NoteInfoEntry._ID)!!

    }

    fun changeCursor(cursor : Cursor?){
        //if(mCursor != null) mCursor!!.close()

        mCursor = cursor
         populateColumnPosition()
        notifyDataSetChanged()


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        var noteResource = LayoutInflater.from(parent.context).inflate(R.layout.noteitem_recycler_layout,parent,false)
        return NotesHolder(noteResource)
    }

    override fun getItemCount(): Int =mCursor?.count?:0

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        mCursor?.moveToPosition(position)
        val course : String?  = mCursor?.getString(mCoursePos)
        val noteTitle : String? = mCursor?.getString(mNoteTitlePos)
        val id : Int? = mCursor?.getInt(mIdPos)

       var note =NoteInfo(null,noteTitle,null,id!!)
        holder.updateView(note!!,course!!)
    }

}