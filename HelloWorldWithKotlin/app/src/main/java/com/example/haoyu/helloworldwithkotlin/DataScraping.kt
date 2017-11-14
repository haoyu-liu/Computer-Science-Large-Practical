package com.example.haoyu.helloworldwithkotlin

/**
 * Created by HAOYU on 2017/10/25.
 */

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.net.URL


class DownloadTask(val progressbar: ProgressBar): AsyncTask<Int, Double, Int>() {

    private val root = Environment.getExternalStorageDirectory()
    private var current = 0
    private var newest = 0
    private val url = "http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/"

    fun formatNum(i : Int): String =
            if(i<10)
                "0"+i.toString()
            else
                i.toString()

    override fun onPreExecute() {
        progressbar.visibility = View.VISIBLE
        progressbar.setProgress(0)
        val root = Environment.getExternalStorageDirectory().absolutePath + "/songlist"
        val songdir = File(root)
/*        if(songdir.exists()){
            val a = songdir.list()
            current = a.size

        }*/
    }

    override fun doInBackground(vararg params: Int?): Int {
        val songlisturl = url + "songs.xml"
        val doc = Jsoup.connect(songlisturl).get()


        val songs = doc.select("Song > Number")
        newest = songs.last().html().toInt()

        if (current <= songs.last().html().toInt()) {

            newest = songs.last().html().toInt()
            try {
                val songdir = File(root.absolutePath + "/songlist")
                if (!songdir.exists()) {
                    Log.d("Datascrap", "not exist")
                    songdir.mkdir()
                }
                //FileUtils.copyURLToFile(URL(songlisturl), File(songdir.absolutePath+"/songs.xml"))

                for (i in current+1..newest) {
                        val mapurl = url + formatNum(i) + "/" + "map5.kml"
                        val lyricsurl = url + formatNum(i) + "/" + "words.txt"
                        FileUtils.copyURLToFile(URL(mapurl), File(songdir.absolutePath, formatNum(i) + "/" + "map5.kml"))
                        FileUtils.copyURLToFile(URL(lyricsurl), File(songdir.absolutePath, formatNum(i) + "/" + "words.txt"))
                        publishProgress(i/(newest-current-1).toDouble())

                }
            }catch (exception: IOException){
                exception.printStackTrace()
            }
        }
        return 1
    }


    override fun onProgressUpdate(vararg values: Double?) {
        val progress = (values[0]!!*100).toInt()
        progressbar.setProgress(progress)
    }

    override fun onPostExecute(result: Int?) {
        progressbar.visibility = View.GONE

    }
}




