package com.example.noteapp.activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.*
import android.view.Gravity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.BuildConfig
import com.example.noteapp.R
import com.example.noteapp.adapters.CourseRecyclerAdapter
import com.example.noteapp.adapters.NotesRecyclerAdapter
import com.example.noteapp.contentproviders.NoteKeeperProviderContract.Companion.Notes
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.CourseInfoEntry
import com.example.noteapp.contracts.NoteKeeperDatabaseContract.Companion.NoteInfoEntry
import com.example.noteapp.dataManager.DataManager
import com.example.noteapp.dataManager.NoteKeeperOpenHelper
import com.example.noteapp.model.CourseInfo
import com.example.noteapp.model.NoteInfo
import com.example.noteapp.services.EXTRA_COURSE_ID
import com.example.noteapp.services.NoteBackupService
import com.example.noteapp.services.NoteUploaderJobService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var notes: MutableList<NoteInfo>?= null
    private var adapter : NotesRecyclerAdapter = NotesRecyclerAdapter()

    private var courses : MutableList<CourseInfo>? = null
    private lateinit var courseAdapter: CourseRecyclerAdapter

    lateinit var linearManager: LinearLayoutManager
    lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var mDbOpenHelper : NoteKeeperOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mDbOpenHelper= NoteKeeperOpenHelper(this)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            startActivity(Intent(this,NoteActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        initializeDisplayContent()
        enableStrictMode()
    }

    private fun enableStrictMode() {
        if(BuildConfig.DEBUG){
            val policy = StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().penaltyLog().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onDestroy() {
        mDbOpenHelper?.close()
        super.onDestroy()
    }

    private fun initializeDisplayContent(){
        DataManager.loadFromDatabase(mDbOpenHelper)
       // notes = DataManager.getInstance()!!.getNotes()
        adapter.mCursor =null

        courses = DataManager.getInstance()?.getCourses()
        courseAdapter = CourseRecyclerAdapter()
        courseAdapter.courseList = courses!!

        linearManager = LinearLayoutManager(this)
        gridLayoutManager = GridLayoutManager(this,resources.getInteger(R.integer.course_grid_span))

        displayNotes()
    }

    fun displayCourses(){
        recycler_note.layoutManager = gridLayoutManager
        recycler_note.adapter = courseAdapter
        selectNavigationMenuItem(R.id.nav_courses)
    }

    private fun selectNavigationMenuItem(id: Int) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu: Menu = navigationView.menu
        menu.findItem(id).setChecked(true)
    }

    private fun displayNotes(){
        recycler_note.layoutManager= linearManager
        recycler_note.adapter = adapter
       selectNavigationMenuItem(R.id.nav_notes )
    }

    val LOAD_NOTES = 1
    override fun onResume() {
        super.onResume()
        //adapter.notifyDataSetChanged()
       //loadNotes()

        @Suppress("DEPRECATION")
        supportLoaderManager.restartLoader(LOAD_NOTES,null,object:LoaderManager.LoaderCallbacks<Cursor>{
            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                if(loader.id ==LOAD_NOTES){
                    adapter.changeCursor(data)
                }
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {
               if(loader.id == LOAD_NOTES){
                   if(adapter.mCursor!=null){
                       adapter.changeCursor(null)
                   }

               }

                //contentResolver.query()
            }

            override fun onCreateLoader(id: Int, args: Bundle?): CursorLoader{
                var loader : CursorLoader? = null
                if(id == LOAD_NOTES ){
                    // loader = loadUpNotes()
                    val notecolumns = arrayOf(
                         Notes._ID,
                        Notes.COLUMN_NOTE_TITLE,
                        Notes.COLUMN_COURSE_TITLE
                    )
                    val noteOrderBy = "${Notes.COLUMN_COURSE_TITLE},${Notes.COLUMN_NOTE_TITLE}"
                    loader =  CursorLoader(this@MainActivity,Notes.CONTENT_EXPANDED_URI,notecolumns,null,null,noteOrderBy)
                }

                return loader!!
            }



        })

        openDrawer()

    }

    private fun openDrawer() {
        val handler :  Handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            run {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        },3000)

    }

    private fun loadUpNotes(): CursorLoader {
        return object: CursorLoader(this@MainActivity){
            override fun loadInBackground(): Cursor? {
                val db: SQLiteDatabase = mDbOpenHelper.readableDatabase
                val notecolumns = arrayOf(
                    NoteInfoEntry.getQName( NoteInfoEntry._ID ),
                    NoteInfoEntry.COLUMN_NOTE_TITLE,
                    CourseInfoEntry.COLUMN_COURSE_TITLE
                )

                val noteOrderBy = "${CourseInfoEntry.COLUMN_COURSE_TITLE},${NoteInfoEntry.COLUMN_NOTE_TITLE}"

                val tablesWithJoin: String ="${NoteInfoEntry.TABLE_NAME} JOIN ${CourseInfoEntry.TABLE_NAME}" +
                        " ON ${NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID)}" +
                        " = ${CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID)}"

                return  db.query(
                    tablesWithJoin,
                    notecolumns,
                    null,null,null,null,noteOrderBy)
            }
        }
    }

    private fun loadNotes() {
        val db: SQLiteDatabase = mDbOpenHelper.readableDatabase
        val notecolumns = arrayOf(
            NoteInfoEntry._ID,
            NoteInfoEntry.COLUMN_COURSE_ID,
            NoteInfoEntry.COLUMN_NOTE_TITLE)

        val noteOrderBy = "${NoteInfoEntry.COLUMN_COURSE_ID},${NoteInfoEntry.COLUMN_NOTE_TITLE}"
        val noteCursor: Cursor = db.query(
            NoteInfoEntry.TABLE_NAME,
            notecolumns,null,null,null,null,noteOrderBy)
          adapter.changeCursor(noteCursor)
    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_backup->{
                backUpNotes()
                true
            }
            R.id.action_upload->{
                scheduleNoteUpload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scheduleNoteUpload() {

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

           val extras :PersistableBundle = PersistableBundle()
           //all extras wished to be passed into the job scheduler class has to be passed here
           extras.putString(NoteUploaderJobService.EXTRA_DATA_URI,Notes.CONTENT_URI.toString())


           val componenetName = ComponentName(this, NoteUploaderJobService::class.java)
           val jobInfo: JobInfo =
               JobInfo.Builder(1,componenetName).
                   //setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).
                   //setRequiresCharging(true).
                   setExtras(extras).
                   build()
            val jobScheduler: JobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
           jobScheduler.schedule(jobInfo)
        }
    }

    private fun backUpNotes() {
      val intentObj: Intent = Intent(this, NoteBackupService::class.java)
        // am just simulating backing up all note with the all string passed in
            intentObj.putExtra(EXTRA_COURSE_ID,"all")
        startService(intentObj)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_notes -> {
               displayNotes()
            }
            R.id.nav_courses-> {
               displayCourses()
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
