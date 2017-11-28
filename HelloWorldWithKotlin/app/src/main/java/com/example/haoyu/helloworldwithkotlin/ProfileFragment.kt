package com.example.haoyu.helloworldwithkotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by HAOYU on 2017/11/28.
 */

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(
                R.layout.layout_profile_fragment, container, false) as ViewGroup
    }
}
