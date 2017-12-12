package com.example.haoyu.helloworldwithkotlin

import android.content.Context
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * This class controls and updates the identity of player by changing
 * "username" value in SharedPreference and profile information on Main2Activity when needed
 *
 * @param context current context for obtaining the instance of SharedPreferences
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

    /**
     * @param view the view where Name and Email need to be changed
     */
    fun updateProfileInfo(view: View){
        val tvName = view.find<TextView>(R.id.profile_name)
        val tvEmail = view.find<TextView>(R.id.profile_email)
        tvName.text = pref.getString("username", "null")
        tvEmail.text = SFileManager(username).getEmail()
    }




}