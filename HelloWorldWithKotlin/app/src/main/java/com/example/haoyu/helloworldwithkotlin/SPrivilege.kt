package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Created by HAOYU on 2017/12/7.
 */
class SPrivilege(context: Context) {

    private val pref = context.getSharedPreferences("user", Context.MODE_PRIVATE)!!
    private val username = pref.getString("username", "null")

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

    fun updateProfileInfo(view: View){
        val tvName = view.find<TextView>(R.id.profile_name)
        val tvEmail = view.find<TextView>(R.id.profile_email)
        tvName.text = pref.getString("username", "null")
        tvEmail.text = SFileManager(username).getEmail()
    }




}