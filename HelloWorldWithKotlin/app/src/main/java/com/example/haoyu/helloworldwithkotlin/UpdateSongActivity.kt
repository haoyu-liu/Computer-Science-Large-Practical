package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.Toolbar
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.w3c.dom.Text
import java.io.IOException

class UpdateSongActivity : AppCompatActivity() {

    private var progressbar:ProgressBar?=null
    private var textview: TextView?=null
    private var current=0
    private var newest=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_song)

        val intent = intent
        current = intent.getIntExtra("current", 0)
        newest = intent.getIntExtra("newest", 0)


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
                progressbar!!.max = 100
                DownloadTask(progressbar!!, textview!!, current, newest).execute(100)
                toast("success!")
                val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor.putInt("songNum", newest)
                editor.apply()
            } catch (e: IOException) {
                toast("download failed")
            }
        }else
            toast("unreadable")
    }

    private fun isExternalStorageWritable():Boolean=
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    private fun isExternalStrorageReadable():Boolean =
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                    Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
}
