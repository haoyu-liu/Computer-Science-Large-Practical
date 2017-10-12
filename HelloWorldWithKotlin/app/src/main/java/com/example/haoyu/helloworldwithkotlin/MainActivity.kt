package com.example.haoyu.helloworldwithkotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    val button = findViewById(R.id.button1) as Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener { View ->
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)

        }
    }




}
