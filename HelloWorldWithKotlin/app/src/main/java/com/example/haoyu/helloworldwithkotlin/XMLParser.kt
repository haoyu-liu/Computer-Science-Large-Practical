package com.example.haoyu.helloworldwithkotlin

import android.os.Environment
import org.jsoup.Jsoup
import java.io.File

/**
 * Created by HAOYU on 2017/11/1.
 */
class SongParser(val song: Int, val version: Int){


    private var songkml = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/"+formatNum(song)+"/map"+version.toString()+".kml")
    private var songtxt = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/01/words.txt")
    val description = hashMapOf<String, String>()
    val markerList= mutableListOf<marker>()
    val lyricsmap = hashMapOf<String, String>()

    fun formatNum(i: Int) :String = if (i<10)
        "0"+i.toString()
    else i.toString()

    init {
        //kml
        val dockml = Jsoup.parse(songkml,"UTF-8")
        val styles = dockml.getElementsByTag("Style")
        styles.forEach { style ->
            description.put(style.attr("id"), style.select("href").html())
        }
        val names = dockml.select("name")
        val descriptions = dockml.select("description")
        val coordinates = dockml.select("coordinates")

        for (i in names.indices){
            val (longitude, latitude, _) = coordinates[i].html().split(",")
            markerList.add(marker(names[i].html(), longitude, latitude, descriptions[i].html()))
        }
        //txt
        val lyrics= mutableListOf<String>()
        songtxt.useLines { lines -> lines.forEach { lyrics.add(it.trim()) }}
        for (i in lyrics.indices){
            val words = lyrics[i].split(*arrayOf(" ", "\t"))
            for (j in words.indices){
                lyricsmap.put((i+1).toString()+":"+j.toString(), words[j].trim(*charArrayOf('?', '.', ',' , ')', '!', '(')))
            }
        }

    }
    fun showmarkers()=
            markerList.forEach {
                println(it.toString())
            }
}

data class marker(val name: String, val longitude: String, val latitude: String, val style: String)