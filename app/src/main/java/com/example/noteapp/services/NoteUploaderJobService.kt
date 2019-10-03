package com.example.noteapp.services

import android.annotation.SuppressLint
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NoteUploaderJobService : JobService() {
    var i = 0
    companion object{
        val EXTRA_DATA_URI = "com.example.noteapp.extras.DATA_URI"
    }
    override fun onStopJob(params: JobParameters?): Boolean {
     if (i != 4500) return true
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        val task = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<JobParameters?,Void,Unit>(){
            override fun doInBackground(vararg bgParams: JobParameters?) {
                val jobParams: JobParameters? = bgParams[0]
                val stringDataIUri: String? = jobParams?.extras?.getString(EXTRA_DATA_URI)
               val dataUri:Uri = Uri.parse(stringDataIUri)
                // use content resolvers here to fetch all rows associated with the uri and trigger you upload to server function
                while(i<=4500){
                    Log.i("jobscheduler",i.toString())
                    i++
                }

                if(i == 4500) jobFinished(jobParams,false)

            }
        }

        task.execute(params)

        return true

    }


}
