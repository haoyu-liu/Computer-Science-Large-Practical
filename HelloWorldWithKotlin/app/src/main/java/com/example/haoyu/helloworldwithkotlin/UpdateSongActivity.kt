package com.example.haoyu.helloworldwithkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.alert
import org.w3c.dom.Text
import java.io.IOException

class UpdateSongActivity : AppCompatActivity() {

    private var progressbar:ProgressBar?=null
    private var textview: TextView?=null
    private var current=0
    private var newest=0
    private var networkChangeReceiver:NetworkChangeReceiver?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_song)

        // Obtain values from previous activity
        val intent = intent
        current = intent.getIntExtra("current", 0)
        newest = intent.getIntExtra("newest", 0)

        // Network monitoring
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver =NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)

        // Initialize widgets
        progressbar = find(R.id.progressBar1)
        textview=find(R.id.textview_progress)
        val myToolBar = find<Toolbar>(R.id.toolbar_updating_songs)
        myToolBar.navigationIcon=resources.getDrawable(R.mipmap.back)
        myToolBar.setOnClickListener{
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        if(isExternalStorageWritable()) {
            try {
                // Start downloading
                progressbar!!.max = 100
                DownloadTask(this, progressbar!!, textview!!, current, newest).execute(100)
                val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor.putInt("songNum", newest)
                editor.apply()
            } catch (e: IOException) {
                snackbar(find(R.id.progressBar1), "download failed")
            }
        }else
            snackbar(find(R.id.progressBar1), "try again.")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }
    private fun isExternalStorageWritable():Boolean=
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    private inner class NetworkChangeReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo==null || !networkInfo.isAvailable){
                longSnackbar(find<RecyclerView>(R.id.recycler_view_timeline), "network unavailable, please check your network")
            }
        }
    }
}
