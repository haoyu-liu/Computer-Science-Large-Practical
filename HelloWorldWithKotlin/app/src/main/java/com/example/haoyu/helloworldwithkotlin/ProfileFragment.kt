package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * Created by HAOYU on 2017/11/28.
 */

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(
                R.layout.layout_profile_fragment, container, false) as ViewGroup
        val pref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val user = pref.getString("username", "admin")
        SPrivilege(context).updateProfileInfo(v)
        return v
    }
}
