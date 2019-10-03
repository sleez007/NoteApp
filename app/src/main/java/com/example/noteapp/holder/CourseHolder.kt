package com.example.noteapp.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.model.CourseInfo
import kotlinx.android.synthetic.main.courseitem_recycler_layout.view.*

class CourseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_course_txt: TextView = itemView.findViewById<TextView>(R.id.text_course)

    fun getDataForBinding(course: CourseInfo){
        tv_course_txt.text = course.mTitle
    }
}