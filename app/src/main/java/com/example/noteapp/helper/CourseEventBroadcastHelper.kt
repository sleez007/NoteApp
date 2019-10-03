package com.example.noteapp.helper

import android.content.Context
import android.content.Intent

class CourseEventBroadcastHelper {
    companion object{
        val ACTION_COURSE_EVENT ="com.example.noteapp.COURSE_EVENT"
        val EXTRA_COURSE_ID = "com.example.noteapp.COURSE_ID"
        val EXTRA_COURSE_MESSAGE = "com.example.noteapp.COURSE_MESSAGE"

        fun sendEventBroadCast(context: Context, courseId: String, message: String){
            val intent: Intent = Intent(ACTION_COURSE_EVENT)
            intent.putExtra(EXTRA_COURSE_ID,courseId)
            intent.putExtra(EXTRA_COURSE_MESSAGE,message)

            context.sendBroadcast(intent)
        }
    }
}