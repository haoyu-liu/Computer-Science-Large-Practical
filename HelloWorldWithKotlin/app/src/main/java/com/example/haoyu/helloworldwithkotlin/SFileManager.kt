package com.example.haoyu.helloworldwithkotlin

import android.os.Environment
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

/**
 * This class manages all files that used in the game, including KML files downloaded from the server,
 * users' information and password and the Unlocked Song List
 *
 * @param user the name of the current player
 *
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

    // Check if the song has been played and guessed correctly. If no, then the song is available.
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

    // Authenticate if input password is correct
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

    // Get user's Email address
    fun getEmail():String{
        val passwdFile = File(root, "passwd.txt")
        for(line in passwdFile.readLines()){
            val(__user, email, _) = line.split(" ")
            if(user == __user)
                return email
        }
        return "s1783038@ed.ac.uk"
    }


    // Update user's timeline record into certain file with the format of XML
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

    // Update user's Unlocked Song List into certain file with the format of XML
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

    // remove the song in the Unlocked Song List
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

    /**
     * the method reads the timeline file in local storage and parses it to the instance of TimelineItem class.
     *
     * @return a list of TimelineItem
     */
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


    /**
     * the method reads the Unlocked Song List file in local storage and parses it to the instance of Song.
     *
     * @return a list of Song's instance
     */
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