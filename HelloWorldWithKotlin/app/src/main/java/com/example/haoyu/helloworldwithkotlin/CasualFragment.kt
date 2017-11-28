package com.example.haoyu.helloworldwithkotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * Created by HAOYU on 2017/11/28.
 */
class CasualFragment:Fragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(
                R.layout.layout_casual_fragment, container, false) as ViewGroup
        val spinner_casual = v.findViewById(R.id.casual_spinner) as Spinner
        var spinneradapter = ArrayAdapter.createFromResource(this.activity, R.array.mode, android.R.layout.simple_spinner_item)
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_casual.adapter = spinneradapter
        return v
    }
}