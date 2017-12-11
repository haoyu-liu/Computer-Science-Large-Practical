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

    fun createUser(email:String, passwd: String): Boolean{
        if(!usrdir.exists()) {
            usrdir.mkdirs()
            val passwdFile = File(root, "passwd.txt")
            passwdFile.appendText("$user $email $passwd\n")
            return true
        }
        return false

    }
    fun isSongAvailable(num:Int):Boolean{
        val unlockedSongListFile = File(usrdir.absolutePath, "USL.xml")
        return if(unlockedSongListFile.exists()) {
            val soup = Jsoup.parse(unlockedSongListFile, "UTF-8")
            val songs = soup.select("index")
            songs.all { it.select("number").html().toInt()!=num }
        }
        else
            true
    }


    fun authenticate(passwd: String): Boolean{
        val passwdFile = File(root, "passwd.txt")
        if(!passwdFile.exists())
            return false
        for(line in passwdFile.readLines()){
            val(__user, __email, __passwd) = line.split(" ")
            if(user == __user && passwd ==__passwd)
                return true
        }
        return false
    }
    fun isProfileExists(f:File):Boolean=f.readLines().any { it.contains(user) }


    fun updateProfile(path :String){
        val profileFile = File(root, "profile.txt")
        if(!profileFile.exists() || !isProfileExists(profileFile))
            profileFile.appendText("$user $path\n")
        else{
            val tempFile = File(root, "temp.file")
            profileFile.forEachLine { line ->
                if(!line.contains(user))
                    tempFile.appendText(line)
                else
                    tempFile.appendText("$user $path\n")
            }
            profileFile.delete()
            tempFile.renameTo(File(root, "profile.txt"))
        }
    }
    fun getProfilePath():String{
        val profileFile = File(root, "profile.txt")
        if(profileFile.exists()) {
            for (line in profileFile.readLines())
                if (line.contains(user)) {
                    val (_, path) = line.split(" ")
                    return path
                }
        }
        return ""
    }
    fun getEmail():String{
        val passwdFile = File(root, "passwd.txt")
        for(line in passwdFile.readLines()){
            val(__user, email, _) = line.split(" ")
            if(user == __user)
                return email
        }
        return "s1783038@ed.ac.uk"
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
	            <site>$link</site>
            </index>
        """.trimIndent()
        unlockedSongListFile.appendText(content)
    }


    fun removeFromUSL(song: Song){
        val number = song.Number
        val unlockedSongListFile = File(usrdir.absolutePath, "USL.xml")
        val soup = Jsoup.parse(unlockedSongListFile, "UTF-8")
        val indices = soup.select("index")
        indices.filter { it.select("number").html()==number }.forEach { it.remove() }

        unlockedSongListFile.writeText(soup.body().html())
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
        val link = song.select("site").html()
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