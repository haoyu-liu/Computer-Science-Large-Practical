package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
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
import top.wefor.circularanim.CircularAnim

/**
 * The fragment for the third page of the ViewPager in Main2Activity.
 * It handles a spinner indicating the degree of difficulty and a button launching MapsActivity for Challenge mode
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

        // Initialize the spinner for choosing the degree of difficulty
        spinner_challenge = v.findViewById(R.id.challenge_spinner) as Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(this.activity, R.array.mode, R.layout.spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_challenge!!.adapter = spinnerAdapter

        return v
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.start_chalg_btn)
        {
            if (!Settings.canDrawOverlays(context)) {
                // Request overlay permission for profile bubble
                alert("Please turn on Overlay permission in Settings", "Overlay permission required") {
                    yesButton {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        startActivity(intent)
                    }
                    noButton {}
                }.show()

            }else {
                // Request network connection when it is unavailable
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if (networkInfo == null || !networkInfo.isAvailable) {
                    alert("Please turn on Internet service and try again", "No Network Connection") {
                        yesButton {}
                        noButton {}
                    }.show()
                } else {
                    // Launch MapsActivity
                    CircularAnim.fullActivity(activity, v)
                            .colorOrImageRes(R.color.lime)
                            .go { startActivity<MapsActivity>("mode" to "Challenge", "degree" to spinner_challenge!!.selectedItem.toString()) }
                }
            }
        }
    }


}