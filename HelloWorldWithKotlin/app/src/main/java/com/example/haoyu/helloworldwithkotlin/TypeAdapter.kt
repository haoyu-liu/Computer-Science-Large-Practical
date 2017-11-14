package com.example.haoyu.helloworldwithkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

/**
 * Created by HAOYU on 2017/11/8.
 */

class TypeAdapter(groups: List<ExpandableGroup<*>>) : ExpandableRecyclerViewAdapter<TypeViewHolder, WordViewHolder>(groups) {

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_type, parent, false)
        return TypeViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_obtained_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindChildViewHolder(holder: WordViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>, childIndex: Int) {

        val artist = (group as Type).items[childIndex]
        holder.setWord(artist.name!!)
    }

    override fun onBindGroupViewHolder(holder: TypeViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder!!.setTypeTitle(group)
    }
}