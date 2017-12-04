package com.example.haoyu.helloworldwithkotlin

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import org.jetbrains.anko.find

/**
 * Created by HAOYU on 2017/11/28.
 */
class CasualFragment:Fragment(), View.OnClickListener{

    private var spinner_casual : Spinner?=null
    private var btn: Button?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(
                R.layout.layout_casual_fragment, container, false) as ViewGroup

        btn = v.find(R.id.start_casl_btn)
        btn!!.setOnClickListener(this)

        spinner_casual = v.findViewById(R.id.casual_spinner) as Spinner
        val spinneradapter = ArrayAdapter.createFromResource(this.activity, R.array.mode, R.layout.spinner_item)
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_casual!!.adapter = spinneradapter
        return v
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.start_casl_btn) {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("mode", "Casual")
            intent.putExtra("degree", spinner_casual!!.selectedItem.toString())
            startActivity(intent)
        }
    }
}