package com.example.haoyu.helloworldwithkotlin

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Created by HAOYU on 2017/11/29.
 */
class TimelineAdapter(val timelineItemList: List<TimelineItem>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>(){



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var cardview_success: CardView?=null
        var cardview_fail:CardView?=null
        var songname:TextView?=null
        var success_time:TextView?=null
        var fail_time:TextView?=null
        var result_icon:ImageView?=null
        init{
            cardview_fail = view.find<CardView>(R.id.fail_cardview)
            cardview_success=view.find<CardView>(R.id.success_cardview)
            songname=view.find<TextView>(R.id.textview_songname)
            success_time=view.find<TextView>(R.id.textview_success_time)
            fail_time=view.find<TextView>(R.id.textview_fail_time)
            result_icon=view.find<ImageView>(R.id.result_image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.layout_timelineitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timelineitem = timelineItemList.get(position)
        if(timelineitem.result=="1"){
            holder.cardview_fail!!.visibility=View.INVISIBLE
            holder.songname!!.setText(timelineitem.song)
            holder.success_time!!.setText(timelineitem.time)
        }else if(timelineitem.result=="0"){
            holder.cardview_success!!.visibility=View.INVISIBLE
            holder.fail_time!!.setText(timelineitem.time)
            holder.result_icon!!.setImageResource(R.mipmap.fail_icon)
        }
    }

    override fun getItemCount(): Int {
        return timelineItemList.size
    }
}