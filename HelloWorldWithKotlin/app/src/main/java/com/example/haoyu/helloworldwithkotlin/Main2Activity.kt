package com.example.haoyu.helloworldwithkotlin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.find
import org.jsoup.Jsoup
import java.io.File

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var mPager:ViewPager?=null
    private var timelineitemlist = ArrayList<TimelineItem>()
    private var recyclerview : RecyclerView?=null
    private var user :String?=null
    //private var imageView: CircleImageView?=null
    private var networkChangeReceiver: NetworkChangeReceiver?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        if(!pref.contains("songNum"))
        {
            val editor = pref.edit()
            editor.putInt("songNum", 0)
            editor.apply()
        }
        user = pref.getString("username", "admin")

        mPager = find(R.id.pager)
        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add("casual", CasualFragment::class.java)
                .add("profile", ProfileFragment::class.java)
                .add("challenge", ChallengeFragment::class.java)
                .create())
        mPager!!.adapter = adapter
        val viewPagerTab = findViewById(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab.setViewPager(mPager)
        mPager!!.currentItem=1

        //imageView =findViewById(R.id.profile_image_nav) as CircleImageView


        //initTimelineItems()
        recyclerview = find(R.id.recycler_view_timeline)
        val layoutmanager = LinearLayoutManager(this)
        recyclerview!!.layoutManager=layoutmanager


        //network monitoring
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver =NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)

        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val headView = navigationView.getHeaderView(0)
        SPrivilege(this).updateProfileInfo(headView)
        navigationView.setNavigationItemSelectedListener(this)


    }

    override fun onResume() {
/*        val imagepath = SFileManager(user!!).getProfilePath()
        if(imagepath!="")
        {
            val uri = Uri.parse(imagepath)
            imageView!!.setImageURI(uri)
        }*/
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if(networkInfo==null || !networkInfo.isAvailable){
            alert("Please turn on Internet service and try again", "No Network Connection") {
                yesButton {}
                noButton {}
            }.show()
        }else {
            val (current, newest) = checkUpdate()
            if (current < newest) {
                startActivity<UpdateSongActivity>("current" to current, "newest" to newest)
            }
        }

        timelineitemlist = SFileManager(user!!).getTI()
        val timelineAdapter = TimelineAdapter(timelineitemlist)
        recyclerview!!.adapter = timelineAdapter
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }


    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            mPager!!.currentItem!=0 -> mPager!!.currentItem = mPager!!.currentItem-1
            else -> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_sync) {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo==null || !networkInfo.isAvailable){
                alert("Please turn on Internet service and try again", "No Network Connection") {
                    yesButton {}
                    noButton {}
                }.show()
            }
            else {
                val (current, newest) = checkUpdate()
                if (current < newest) {
                    startActivity<UpdateSongActivity>("current" to current, "newest" to newest)
                } else {
                    snackbar(find<RecyclerView>(R.id.recycler_view_timeline), "no new update")
                }
            }
        }
        else if (id == R.id.nav_unlockedsong) {
            startActivity<UnlockedSongActivity>()
        }
        else if (id == R.id.nav_manage) {
            val intentFromGallery = Intent()
            intentFromGallery.type = "image/*";
            intentFromGallery.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(intentFromGallery, 6666)
        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {
            SPrivilege(this).updateUser("null")
            startActivity<MainActivity>()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun isExternalStorageWritable():Boolean=
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())

    fun isExternalStrorageReadable():Boolean =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())

    private fun checkUpdate():ArrayList<Int>{
        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        var a = 0
        val thread = Thread {
            val soup = Jsoup.connect("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml").get()
            val songs = soup.select("Song > Number")
            val newest = songs.last().html().toInt()
            a=newest
        }
        thread.start()
        thread.join()
        val songlistDir = File(Environment.getExternalStorageDirectory().absolutePath, "songlist")
        var current = pref.getInt("songNum", 0)
        if(!songlistDir.exists()||songlistDir.list().size != current)
            current=0
        return (arrayListOf(current, a))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        if (resultCode == Activity.RESULT_CANCELED) {
            snackbar(find<RecyclerView>(R.id.recycler_view_timeline), "no image selected")
            return
        }
        val uri = intent!!.data
        SFileManager(user!!).updateProfile(uri.path)
        super.onActivityResult(requestCode, resultCode, intent)
    }




    private inner class NetworkChangeReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo==null || !networkInfo.isAvailable){
                longSnackbar(find<RecyclerView>(R.id.recycler_view_timeline), "network unavailable, please check your network")
            }
        }
    }
}
