package com.example.haoyu.helloworldwithkotlin


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jsoup.Jsoup
import java.io.File

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var mPager:ViewPager?=null
    private var timelineitemlist = ArrayList<TimelineItem>()
    private var recyclerview : RecyclerView?=null
    private var user :String?=null
    private var tv1:TextView?=null
    private var tv2:TextView?=null
    private var networkChangeReceiver: NetworkChangeReceiver?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize and configure Toolbar, Drawer and NavigationView in Main2Activity
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val headView = navigationView.getHeaderView(0)
        SPrivilege(this).updateProfileInfo(headView)
        navigationView.setNavigationItemSelectedListener(this)


        // Get the current player's name and the number of songs
        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        if(!pref.contains("songNum")) {
            val editor = pref.edit()
            editor.putInt("songNum", 0)
            editor.apply()
        }
        user = pref.getString("username", "admin")


        // Initialize ViewPager
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



        // Initialize RecyclerView for displaying player's timeline
        recyclerview = find(R.id.recycler_view_timeline)
        tv1 = find(R.id.textview_cc)
        tv2 = find(R.id.textview_dd)
        val layoutmanager = LinearLayoutManager(this)
        recyclerview!!.layoutManager=layoutmanager


        // Network monitoring
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        networkChangeReceiver =NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, intentFilter)

        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        /**
         * Check network is available and check if there are new songs on the server
         * every time entering Main2Activity
         */
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

        // Update items on the Timeline when user is back to Main2Activity
        timelineitemlist = SFileManager(user!!).getTI()
        if(timelineitemlist.size!=0) {
            tv1!!.visibility= View.INVISIBLE
            tv2!!.visibility=View.INVISIBLE
        }
        val timelineAdapter = TimelineAdapter(timelineitemlist)
        recyclerview!!.adapter = timelineAdapter
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
            else -> {
                // Log out and launch MainActivity
                SPrivilege(this).updateUser("null")
                startActivity<MainActivity>()}
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_sync) {
            // Check if there are new songs on the server.
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
            // Launch UnlockedSongActivity
            startActivity<UnlockedSongActivity>()
        }
        else if (id == R.id.nav_send) {
            // Log out and launch MainActivity
            SPrivilege(this).updateUser("null")
            startActivity<MainActivity>()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * This method connects to the server, parse songs.xml and acquire the latest number of songs
     *
     * @return  a set of Int {current, newest}
     * current: the number of songs in local storage
     * newest: the number of songs on the server
     */
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

    /**
     * This method requests read, write and location permission
     */
    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

    }


    /**
     * The inner class monitors any network change and
     * notify the player if network is unavailable
     */
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
