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
            startActivity<Main2Activity>()
    }

    /**
     * The method handles the click event of floating action button(for register activity)
     * and login button
     */
    override fun onClick(view: View) {
        when (view.id) {

            R.id.fab -> {
                window.exitTransition = null
                window.enterTransition = null
                val options = ActivityOptions.makeSceneTransitionAnimation(this, fab, fab!!.transitionName)
                startActivity(Intent(this, RegisterActivity::class.java), options.toBundle())
            }
            R.id.bt_go -> {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo

                if(networkInfo==null || !networkInfo.isAvailable){
                    // Request network connection when it is unavailable
                    alert("Please turn on Internet service and try again", "No Network Connection") {
                        yesButton {}
                        noButton {}
                    }.show()
                }
                else {
                    val username = etUsername!!.text.toString()
                    val password = etPassword!!.text.toString()
                    val sfileManager = SFileManager(username)

                    //Authenticate password
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


    /**
     * This method requests read, write and location permission
     */
    private fun requestPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

}
