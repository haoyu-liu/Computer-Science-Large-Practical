package com.example.haoyu.helloworldwithkotlin

/**
 *  This class for each type of word, such as "very interesting", in Expandable RecyclerView
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
        return when(mtitle){
            "boring" -> 0
            "notboring" -> 1
            "interesting" -> 2
            "veryinteresting" -> 3
            else -> 4
        }
    }
}
