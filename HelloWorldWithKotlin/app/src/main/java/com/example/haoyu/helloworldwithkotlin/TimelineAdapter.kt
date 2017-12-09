package com.example.haoyu.helloworldwithkotlin

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find
import android.util.TypedValue



/**
 * Created by HAOYU on 2017/11/29.
 */
class TimelineAdapter(private val timelineItemList: List<TimelineItem>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>(){



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val cdSuccess=view.find<CardView>(R.id.success_cardview)
        val cvFail=view.find<CardView>(R.id.fail_cardview)
        val tvSongName=view.find<TextView>(R.id.textview_songname)
        val tvSuccessTime=view.find<TextView>(R.id.textview_success_time)
        val tvFailTime=view.find<TextView>(R.id.textview_fail_time)
        val ivResultIcon=view.find<ImageView>(R.id.result_image)
        val tvSongNameFail=view.find<TextView>(R.id.textview_songname_fail)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_timelineitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timelineItem = timelineItemList.get(position)
        if(timelineItem.result=="1"){
            holder.cvFail.visibility=View.INVISIBLE
            holder.tvSongName.text = timelineItem.song
            holder.tvSuccessTime.text = timelineItem.time
        }else if(timelineItem.result=="0"){
            holder.cdSuccess.visibility=View.INVISIBLE
            holder.tvSongNameFail.text=timelineItem.song
            holder.tvFailTime.text = timelineItem.time
            holder.ivResultIcon.setImageResource(R.mipmap.fail_icon)
        }
    }

    override fun getItemCount(): Int = timelineItemList.size
}