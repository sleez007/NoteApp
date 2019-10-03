package com.example.noteapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.holder.CourseHolder
import com.example.noteapp.model.CourseInfo

class CourseRecyclerAdapter : RecyclerView.Adapter<CourseHolder>() {
    var courseList : MutableList<CourseInfo> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.courseitem_recycler_layout,parent,false)
        return CourseHolder(layout)
    }

    override fun getItemCount(): Int =  courseList.size

    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
        val data = courseList.get(position)
        holder.getDataForBinding(data)
    }

}