package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import co.dift.ui.SwipeToAction
import org.jetbrains.anko.layoutInflater

/**
 * Created by HAOYU on 2017/11/6.
 */


class SongAdapter(val msonglist: List<Song>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    inner class SongViewHolder(view: View): SwipeToAction.ViewHolder<Song>(view){
        var title : TextView? =null
        var artist: TextView?=null
        init{
            title = view.findViewById(R.id.title) as TextView
            artist = view.findViewById(R.id.artist) as TextView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val song = msonglist.get(position)
        val svh = holder as SongViewHolder
        svh!!.title!!.setText(song.Title)
        svh!!.artist!!.setText(song.Artist)
        svh.data=song
    }

    override fun getItemCount(): Int {
        return msonglist.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}