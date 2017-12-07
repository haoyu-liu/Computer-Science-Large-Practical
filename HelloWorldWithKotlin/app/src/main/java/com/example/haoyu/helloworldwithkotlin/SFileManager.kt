package com.example.haoyu.helloworldwithkotlin

import android.os.Environment
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

/**
 * Created by HAOYU on 2017/12/5.
 */
class SFileManager(val user : String) {

    private val root = Environment.getExternalStorageDirectory().absolutePath +"/songlefile"
    private val rootdir = File(root)
    private val usrdir = File(rootdir.absolutePath, user)

    init{
        if(!rootdir.exists())
            rootdir.mkdirs()
        val admindir = File(rootdir.absolutePath, "admin")
        if(!admindir.exists())
            admindir.mkdirs()
    }

    fun isUserExists():Boolean{
        val passwdFile = File(root, "passwd.txt")
        if(!passwdFile.exists())
            return false
        return passwdFile.readLines().any { it.contains(user) }
    }

    fun createUser(passwd: String): Boolean{
        if(!usrdir.exists()) {
            usrdir.mkdirs()
            val passwdFile = File(root, "passwd.txt")
            passwdFile.appendText("$user $passwd")
            return true
        }
        return false

    }


    fun authenticate(passwd: String): Boolean{
        val passwdFile = File(root, "passwd.txt")
        if(!passwdFile.exists())
            return false
        for(line in passwdFile.readLines()){
            val(__user, __passwd) = line.split(" ")
            if(user == __user && passwd ==__passwd)
                return true
        }
        return false
    }


    fun updateTI(timelineItem: TimelineItem){
        val time = timelineItem.time
        val result = timelineItem.result
        val song = timelineItem.song

        val timelineFile = File(usrdir.absolutePath, "timeline.xml")
        val content = """
            <item>
	            <index>$song</index>
	            <result>$result</result>
	            <time>$time</time>
            </item>
        """.trimIndent()
        timelineFile.appendText(content)
    }


    fun updateUSL(song: Song){
        val number =song.Number
        val artist = song.Artist
        val title = song.Title
        val link = song.Link

        val unlockedSongListFile = File(usrdir.absolutePath, "USL.xml")
        val content = """
            <index>
	            <number>$number</number>
	            <artist>$artist</artist>
	            <title>$title</title>
	            <link>$link</link>
            </index>
        """.trimIndent()
        unlockedSongListFile.appendText(content)
    }


    private fun parseSingleItem(item: Element): TimelineItem{
        val time = item.select("time").html()
        val result = item.select("result").html()
        val song= item.select("index").html()
        return TimelineItem(time, result, song)
    }


    fun getTI(): ArrayList<TimelineItem>{
        val timelineFile = File(usrdir.absolutePath, "timeline.xml")
        return if(timelineFile.exists()) {
            val soup = Jsoup.parse(timelineFile, "UTF-8")
            val timelineItemList = ArrayList<TimelineItem>()
            val items = soup.select("item")
            items.forEach { v ->
                timelineItemList.add(parseSingleItem(v))
            }
            timelineItemList.reverse()
            timelineItemList
        }
        else
            ArrayList<TimelineItem>()
    }


    private fun parseSingleSong(song: Element):Song{
        val number = song.select("number").html()
        val artist = song.select("artist").html()
        val title = song.select("title").html()
        val link = song.select("link").html()
        return Song(number, artist, title, link)
    }


    fun getUSL(): MutableList<Song>{
        val unlockedSongListFile = File(usrdir.absolutePath, "USL.xml")
        return if(unlockedSongListFile.exists()) {
            val soup = Jsoup.parse(unlockedSongListFile, "UTF-8")
            val songlist = mutableListOf<Song>()
            val songs = soup.select("index")
            songs.forEach { v ->
                songlist.add(parseSingleSong(v))
            }
            songlist
        }
        else
            mutableListOf<Song>()
    }
}