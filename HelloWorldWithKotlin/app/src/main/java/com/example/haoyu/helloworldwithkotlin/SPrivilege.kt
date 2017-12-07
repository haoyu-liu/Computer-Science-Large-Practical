package com.example.haoyu.helloworldwithkotlin

import android.content.Context

/**
 * Created by HAOYU on 2017/12/7.
 */
class SPrivilege(context: Context) {

    val pref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    fun isUserLogined():Boolean{

        if(pref.getString("username", "null") != "null")
            return true
        return false
    }

    fun updateUser(username: String){
        val editor = pref.edit()
        editor.putString("username", username)
        editor.apply()
    }


}