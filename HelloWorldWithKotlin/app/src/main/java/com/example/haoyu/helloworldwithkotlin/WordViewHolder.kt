package com.example.haoyu.helloworldwithkotlin

import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import org.jetbrains.anko.find
import org.w3c.dom.Text

/**
 * Created by HAOYU on 2017/11/8.
 */
class WordViewHolder(itemView: View): ChildViewHolder(itemView){
    private var childTextView :TextView? =null

    init {
        childTextView = itemView.findViewById(R.id.item_word) as TextView
    }
    fun setWord(word: String) = childTextView!!.setText(word)
}


class TypeViewHolder(itemView: View): GroupViewHolder(itemView){

    private val typename=itemView.find<TextView>(R.id.list_item_type_name)
    private val arrow=itemView.find<ImageView>(R.id.list_item_type_arrow)


    fun setTypeTitle(type: ExpandableGroup<*>?){
        typename.text = type!!.title
    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand(){
        val rotate = RotateAnimation(360F, 180F, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration=300
        rotate.fillAfter=true
        arrow.animation=rotate

    }

    private fun animateCollapse(){
        val rotate = RotateAnimation(180F, 360F, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration=300
        rotate.fillAfter=true
        arrow.animation=rotate

    }
}