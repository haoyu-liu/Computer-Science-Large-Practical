package com.example.haoyu.helloworldwithkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.transition.Explode
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.*
import top.wefor.circularanim.CircularAnim

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private var fab: FloatingActionButton? = null
    private var cvAdd: CardView? = null
    private var btRegGo: Button?=null
    private var etUsername: EditText?=null
    private var etEmail: EditText?=null
    private var etPassword:EditText?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fab = find(R.id.fab)
        cvAdd = find(R.id.cv_add)
        btRegGo = find(R.id.bt_reg_go)
        etUsername = find(R.id.et_username)
        etEmail = find(R.id.et_email)
        etPassword = find(R.id.et_password)
        fab!!.setOnClickListener(this)
        btRegGo!!.setOnClickListener(this)

        ShowEnterAnimation()

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fab -> animateRevealClose()
            R.id.bt_reg_go -> {

                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if(networkInfo==null || !networkInfo.isAvailable){
                    //Ensure network connection
                    alert("Please turn on Internet service and try again", "No Network Connection") {
                        yesButton {}
                        noButton {}
                    }.show()
                }
                else {
                    val username = etUsername!!.text.toString()
                    val email = etEmail!!.text.toString()
                    val password = etPassword!!.text.toString()
                    if(!username.contains(" ") && !email.contains(" ") && !password.contains(" ")) {
                        val sfileManager = SFileManager(username)
                        if (!sfileManager.isUserExists()) {
                            sfileManager.createUser(email, password)
                            SPrivilege(this).updateUser(username)

                            //Animation
                            CircularAnim.fullActivity(this, v)
                                    .colorOrImageRes(R.color.even_dark)
                                    .go { startActivity<Main2Activity>() }
                        } else
                          toast("username existed.")
                    } else
                        toast("input cannot contain space.")

                }
            }
        }
    }

    private fun ShowEnterAnimation() {
        val transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition)
        window.sharedElementEnterTransition = transition

        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                cvAdd!!.visibility = View.GONE
            }

            override fun onTransitionEnd(transition: Transition) {
                transition.removeListener(this)
                animateRevealShow()
            }

            override fun onTransitionCancel(transition: Transition) {

            }

            override fun onTransitionPause(transition: Transition) {

            }

            override fun onTransitionResume(transition: Transition) {

            }


        })
    }

    fun animateRevealShow() {
        val mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd!!.width / 2, 0, (fab!!.width / 2).toFloat(), cvAdd!!.height.toFloat())
        mAnimator.duration = 500
        mAnimator.interpolator = AccelerateInterpolator()
        mAnimator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                cvAdd!!.visibility = View.VISIBLE
                super.onAnimationStart(animation)
            }
        })
        mAnimator.start()
    }

    fun animateRevealClose() {
        val mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd!!.width / 2, 0, cvAdd!!.height.toFloat(), (fab!!.width / 2).toFloat())
        mAnimator.duration = 500
        mAnimator.interpolator = AccelerateInterpolator()
        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                cvAdd!!.visibility = View.INVISIBLE
                super.onAnimationEnd(animation)
                fab!!.setImageResource(R.drawable.plus)
                super@RegisterActivity.onBackPressed()
            }
        })
        mAnimator.start()
    }

    override fun onBackPressed() {
        animateRevealClose()
    }


}
