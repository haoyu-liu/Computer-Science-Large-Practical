package com.example.haoyu.helloworldwithkotlin

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.*
import top.wefor.circularanim.CircularAnim

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var btGo: Button? = null
    private var cv: CardView? = null
    private var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etUsername = find(R.id.et_username)
        etPassword = find(R.id.et_password)
        btGo = find(R.id.bt_go)
        cv = find(R.id.cv)
        fab = find(R.id.fab)
        btGo!!.setOnClickListener(this)
        fab!!.setOnClickListener(this)
        requestPermission()
        if(SPrivilege(this).isUserLogined())
            startActivity(Intent(this, Main2Activity::class.java))
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab -> {
                window.exitTransition = null
                window.enterTransition = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val options = ActivityOptions.makeSceneTransitionAnimation(this, fab, fab!!.transitionName)
                    startActivity(Intent(this, RegisterActivity::class.java), options.toBundle())
                } else {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
            R.id.bt_go -> {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if(networkInfo==null || !networkInfo.isAvailable){
                    alert("Please turn on Internet service and try again", "No Network Connection") {
                        yesButton {}
                        noButton {}
                    }.show()
                }
                else {
                    val username = etUsername!!.text.toString()
                    val password = etPassword!!.text.toString()
                    val sfileManager = SFileManager(username)
                    if (sfileManager.authenticate(password)) {
                        SPrivilege(this).updateUser(username)
                        CircularAnim.fullActivity(this, view)
                                .colorOrImageRes(R.color.bg)
                                .go { startActivity<Main2Activity>() }
                    }else{
                        toast("incorrect username or password")
                    }
                }
            }
        }
    }


    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    private fun checkPermission() =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED


    fun isExternalStorageWritable():Boolean=
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())

    fun isExternalStrorageReadable():Boolean =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())




}
