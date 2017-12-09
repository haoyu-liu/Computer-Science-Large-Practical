package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.yesButton

/**
 * Created by HAOYU on 2017/11/28.
 */
class ChallengeFragment: Fragment(), View.OnClickListener{

    private var spinner_challenge: Spinner? =null
    private var btn:Button?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(
                R.layout.layout_challenge_fragment, container, false) as ViewGroup

        btn = v.find(R.id.start_chalg_btn)
        btn!!.setOnClickListener(this)

        spinner_challenge = v.findViewById(R.id.challenge_spinner) as Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(this.activity, R.array.mode, R.layout.spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_challenge!!.adapter = spinnerAdapter

        return v
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.start_chalg_btn)
        {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo==null || !networkInfo.isAvailable){
                alert("Please turn on Internet service and try again", "No Network Connection") {
                    yesButton {}
                    noButton {}
                }.show()
            }else {
                startActivity<MapsActivity>("mode" to "Challenge", "degree" to spinner_challenge!!.selectedItem.toString())
            }
        }
    }


}