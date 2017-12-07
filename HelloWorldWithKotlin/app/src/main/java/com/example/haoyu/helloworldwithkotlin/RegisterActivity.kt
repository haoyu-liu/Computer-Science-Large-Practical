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
import android.os.Build
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.transition.Explode
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.find
import java.io.File

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private var fab: FloatingActionButton? = null
    private var cvAdd: CardView? = null
    private var bt_reg_go: Button?=null
    private var et_username: EditText?=null
    private var et_password: EditText?=null
    private var et_repeatpassword:EditText?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fab = find(R.id.fab)
        cvAdd = find(R.id.cv_add)
        bt_reg_go = find(R.id.bt_reg_go)
        et_username = find(R.id.et_username)
        et_password = find(R.id.et_password)
        et_repeatpassword = find(R.id.et_repeatpassword)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation()
        }
        fab!!.setOnClickListener(this)
        bt_reg_go!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fab -> animateRevealClose()
            R.id.bt_reg_go -> {
                val username = et_username!!.text.toString()
                val password = et_password!!.text.toString()
                val reapeatpassword = et_repeatpassword!!.text.toString()
                val sfileManager = SFileManager(username)

                if(password==reapeatpassword && (!sfileManager.isUserExists())) {
                    sfileManager.createUser(password)
                    SPrivilege(this).updateUser(username)

                    //animation
                    val explode = Explode()
                    explode.duration = 500
                    window.exitTransition = explode
                    window.enterTransition = explode
                    val oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                    val i2 = Intent(this, Main2Activity::class.java)
                    startActivity(i2, oc2.toBundle())
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
