package com.example.haoyu.helloworldwithkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.dift.ui.SwipeToAction
import org.jetbrains.anko.toast

class UnlockedSongActivity : AppCompatActivity() {

    private var songlist : MutableList<Song>? = null
    private var adapter: SongAdapter? =null
    var swipeToAction: SwipeToAction? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_song)
        val mytoolbar = findViewById(R.id.my_toolbar) as android.support.v7.widget.Toolbar
        mytoolbar.setNavigationIcon(resources.getDrawable(R.mipmap.back))
        mytoolbar.setOnClickListener{
            finish()
        }
        initSong()

        val recyclerview = findViewById(R.id.recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager
        recyclerview.setHasFixedSize(true)

        adapter = SongAdapter(songlist!!)
        recyclerview.adapter = adapter

        swipeToAction = SwipeToAction(recyclerview, object: SwipeToAction.SwipeListener<Song>{
            override fun onClick(itemData: Song?) {
                toast("click")
                Log.d("left", itemData.toString())
            }

            override fun onLongClick(itemData: Song?) {
                toast("longclick")
            }

            override fun swipeRight(itemData: Song?): Boolean {
                toast("swiperight")
                return true
            }

            override fun swipeLeft(itemData: Song?): Boolean {
                removeSong(itemData!!)
                toast("removed")
                return true
            }
        })






    }

    private fun initSong() {
        val thread = Thread({
            songlist = SongParser(1, 1).loadSongList()
        })
        thread.start()
        thread.join()
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.unlock_toolbar ,menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.home -> finish()
            else -> toast("hello")
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun removeSong(song: Song):Int{
        val pos = songlist!!.indexOf(song)
        songlist!!.remove(song)
        adapter!!.notifyItemRemoved(pos)
        return pos
    }

}
