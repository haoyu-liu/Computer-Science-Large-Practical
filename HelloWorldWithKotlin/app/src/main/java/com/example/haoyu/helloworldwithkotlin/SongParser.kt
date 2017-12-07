package com.example.haoyu.helloworldwithkotlin

import android.os.Environment
import org.jsoup.Jsoup
import java.io.File

/**
 * Created by HAOYU on 2017/11/1.
 */

data class Song(val Number: String, val Artist: String, val Title: String, val Link: String)
data class marker(val name: String, val longitude: String, val latitude: String, val style: String)


class SongParser(val index: Int, val version: Int){


    private var songkml = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/${formatNum(index)}/map${version.toString()}.kml")
    private var songtxt = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/${formatNum(index)}/words.txt")
    private var songlistxml = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/songs.xml")
    private val description = hashMapOf<String, String>()
    private val markerList= mutableListOf<marker>()
    val lyricsmap = hashMapOf<String, String>()
    val songlist = mutableListOf<Song>()
    var song:Song?=null

    fun formatNum(i: Int) :String = if (i<10)
        "0"+i.toString()
    else i.toString()

    init{
        val thread = Thread{
            loadSongList()
        }
        thread.start()
        thread.join()
        song = songlist[index -1]
    }

    fun loadMarkerList() : MutableList<marker>{
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
        return markerList

    }

    fun loadSongList() : MutableList<Song>{
        val xml = Jsoup.connect("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml").get()
        val Songs = xml.select("Song")
        Songs.forEach { song ->
            val number = song.select("Number").html()
            val artist = song.select("Word").html()
            val title = song.select("Title").html()
            val link = song.select("Link").html()
            songlist.add(Song(number, artist, title, link))

        }
        return songlist
    }
}

