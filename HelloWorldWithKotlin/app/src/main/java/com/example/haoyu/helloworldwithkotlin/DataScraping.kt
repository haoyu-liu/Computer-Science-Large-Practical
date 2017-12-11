package com.example.haoyu.helloworldwithkotlin

/**
 * Created by HAOYU on 2017/10/25.
 */

import android.app.Activity
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.net.URL
import org.jetbrains.anko.*


class DownloadTask(val activity : Activity,val progressbar: ProgressBar, val textview:TextView, val current:Int, val newest:Int): AsyncTask<Int, Double, Int>() {

    private val root = Environment.getExternalStorageDirectory()

    private val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/"

    private fun formatNum(i : Int): String =
            if(i<10)
                "0"+i.toString()
            else
                i.toString()

    override fun onPreExecute() {
        progressbar.visibility = View.VISIBLE
        progressbar.progress = 0
        textview.text = "downloading: 0 / ${newest-current}"
    }

    override fun doInBackground(vararg params: Int?): Int {
        val songlistUrl = url + "songs.xml"
        val doc = Jsoup.connect(songlistUrl).get()
        val songs = doc.select("Song > Number")
        if (current <= newest) {
            try {
                val songdir = File(root.absolutePath,  "songlist")
                if (!songdir.exists()) {
                    Log.d("Datascrap", "not exist")
                    songdir.mkdir()
                }
                for (i in current+1..newest) {
                    val map5url = url + formatNum(i) + "/" + "map5.kml"
                    val map4url = url +formatNum(i) +"/"+"map4.kml"
                    val lyricsUrl = url + formatNum(i) + "/" + "words.txt"
                    FileUtils.copyURLToFile(URL(map5url), File(songdir.absolutePath, formatNum(i) + "/" + "map5.kml"))
                    FileUtils.copyURLToFile(URL(map4url), File(songdir.absolutePath, formatNum(i) + "/" + "map4.kml"))
                    FileUtils.copyURLToFile(URL(lyricsUrl), File(songdir.absolutePath, formatNum(i) + "/" + "words.txt"))
                    publishProgress(i/(newest-current-1).toDouble(), i.toDouble())
                }
            }catch (exception: IOException){
                exception.printStackTrace()
            }
        }
        return 1
    }


    override fun onProgressUpdate(vararg values: Double?) {
        val progress = (values[0]!!*100).toInt()
        progressbar.progress = progress
        textview.text="downloading: ${values[1]!!.toInt()} / ${newest-current}"
    }

    override fun onPostExecute(result: Int?) {
        //progressbar.visibility = View.GONE
        progressbar.progress = 100
        textview.text="complete!"
        activity.finish()
    }
}







