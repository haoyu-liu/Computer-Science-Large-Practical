package com.example.haoyu.helloworldwithkotlin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.find


/**
 * The adapter for RecyclerView of the Timeline
 */
class TimelineAdapter(private val timelineItemList: List<TimelineItem>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>(){



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        // Widgets of a success timeline item, showing at the right side of timeline
        val tvSongName=view.find<TextView>(R.id.textview_songname)
        val tvUnlock=view.find<TextView>(R.id.textview_unlock)
        val tvSuccessTime=view.find<TextView>(R.id.textview_success_time)
        val ivResultIcon=view.find<ImageView>(R.id.result_image)

        // Widgets of a fail timeline item, showing at the left side of timeline
        val tvFailTime=view.find<TextView>(R.id.textview_fail_time)
        val tvSongNameFail=view.find<TextView>(R.id.textview_songname_fail)
        val tvFail=view.find<TextView>(R.id.textview_fail)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_timelineitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timelineItem = timelineItemList.get(position)
        // Set texts for widgets
        if(timelineItem.result=="1"){
            holder.tvUnlock.text="Unlock"
            holder.tvSongName.text = timelineItem.song
            holder.tvSuccessTime.text = timelineItem.time
            holder.ivResultIcon.setImageResource(R.mipmap.success_icon)
            holder.tvFail.text=""
            holder.tvSongNameFail.text=""
            holder.tvFailTime.text = ""
        }else if(timelineItem.result=="0"){
            holder.tvFail.text="Fail"
            holder.tvSongNameFail.text=timelineItem.song
            holder.tvFailTime.text = timelineItem.time
            holder.ivResultIcon.setImageResource(R.mipmap.fail_icon)
            holder.tvUnlock.text=""
            holder.tvSongName.text = ""
            holder.tvSuccessTime.text = ""
        }
    }

    override fun getItemCount(): Int = timelineItemList.size
}