package com.example.haoyu.helloworldwithkotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import co.dift.ui.SwipeToAction
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import top.wefor.circularanim.CircularAnim

/**
 *  the Adapter for RecyclerView in UnlockedSongActivity
 */


class SongAdapter(private val msonglist: MutableList<Song>, private val activity: Activity) : RecyclerView.Adapter<SongAdapter.ViewHolder>(){




    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val title = view.find<TextView>(R.id.title)
        val artist = view.find<TextView>(R.id.artist)
        val tube = view.find<ImageButton>(R.id.youtube_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val song = msonglist[position]
        holder!!.title.text = song.Title
        holder.artist.text = song.Artist
        val pref = activity.getSharedPreferences("user", Context.MODE_PRIVATE)
        val user = pref.getString("username", "admin")
        // Intent to YouTube
        holder.tube.setOnClickListener { v ->
            CircularAnim.fullActivity(activity, v)
                    .colorOrImageRes(R.color.red)
                    .go{ activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(song.Link))) }
        }
        // Remove song from USL
        holder.view.setOnLongClickListener { v->
            activity.alert("Are you sure you want to remove this song from the list?", "Song deletion"){
                yesButton {
                    SFileManager(user).removeFromUSL(song)
                    msonglist.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                }
                noButton {  }
            }.show()
            true
        }
    }

    override fun getItemCount(): Int = msonglist.size

    override fun getItemViewType(position: Int): Int = 0
}