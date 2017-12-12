package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.dift.ui.SwipeToAction
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class UnlockedSongActivity : AppCompatActivity() {

    private var songlist : MutableList<Song>? = null
    private var adapter: SongAdapter? =null
    private var swipeToAction: SwipeToAction? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_song)
        val myToolBar = find<Toolbar>(R.id.my_toolbar)
        myToolBar.navigationIcon=resources.getDrawable(R.mipmap.back)
        myToolBar.setOnClickListener{
            finish()
        }
        // Get songs list that each song has been unlocked
        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val user = pref.getString("username", "admin")
        songlist = SFileManager(user).getUSL()

        // Initialize RecyclerView
        val recyclerview = find<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager
        recyclerview.setHasFixedSize(true)
        adapter = SongAdapter(songlist!!)
        recyclerview.adapter = adapter

        // Set swiping actions for each item in RecyclerView
        swipeToAction = SwipeToAction(recyclerview, object: SwipeToAction.SwipeListener<Song>{
            override fun onClick(itemData: Song?) {
                toast("click")
                Log.d("left", itemData.toString())
            }

            override fun onLongClick(itemData: Song?) {
                toast("longclick")
            }

            override fun swipeRight(itemData: Song?): Boolean {
                SFileManager(user).removeFromUSL(itemData!!)
                removeSong(itemData)
                snackbar(find<RecyclerView>(R.id.recycler_view), "removed")
                return true
            }

            override fun swipeLeft(itemData: Song?): Boolean {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itemData!!.Link)))
                toast("swiperight")
                return true
            }
        })
    }




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

    // Remove song from the Unlocked Song List
    fun removeSong(song: Song):Int{
        val pos = songlist!!.indexOf(song)
        songlist!!.remove(song)
        adapter!!.notifyItemRemoved(pos)
        return pos
    }

}
