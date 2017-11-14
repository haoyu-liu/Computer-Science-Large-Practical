package com.example.haoyu.helloworldwithkotlin

/**
 * Created by HAOYU on 2017/11/8.
 */

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class Type(title: String, items: List<Word>) : ExpandableGroup<Word>(title, items) {

    val mtitle = title

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Type) return false

        val type = o as Type?

        return title == type!!.title

    }

    override fun hashCode(): Int {
        when(mtitle){
            "boring" -> return 0
            "notboring" ->return 1
            "interesting" -> return 2
            "veryinteresting" -> return 3
            else -> return 4
        }
    }
}
