package com.example.haoyu.helloworldwithkotlin

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import org.jetbrains.anko.find

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var mPager:ViewPager?=null
    private var timelineitemlist = ArrayList<TimelineItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mPager = findViewById(R.id.pager) as ViewPager
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
        initTimelineItems()
        val recyclerview = find<RecyclerView>(R.id.recycler_view_timeline)
        val layoutmanager = LinearLayoutManager(this)
        recyclerview.layoutManager=layoutmanager
        val timeline_adapter = TimelineAdapter(timelineitemlist)
        recyclerview.adapter = timeline_adapter


/*        mPagerApdater = SlidePagerAdapter(supportFragmentManager)
        mPager!!.adapter=mPagerApdater
        val viewPagerTab = findViewById(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab.setViewPager(mPager)*/

        //Progress Bar implementation
/*        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val progressbar = findViewById(R.id.progressBar1) as ProgressBar
            if(isExternalStorageWritable()) {
                try {

                    progressbar.max = 100
                    DownloadTask(progressbar).execute(100)
                } catch (e: IOException) {
                    Toast.makeText(this, "download failed", Toast.LENGTH_SHORT).show()
                }

            }else
                Toast.makeText(this, "unreadable", Toast.LENGTH_SHORT).show()



        }*/

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)




    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if(mPager!!.currentItem!=0)
            mPager!!.setCurrentItem(mPager!!.currentItem-1) else{
            super.onBackPressed()
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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_unlockedsong) {
            val intent = Intent(this, UnlockedSongActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

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

    private fun initTimelineItems(){
        timelineitemlist.add(TimelineItem("2015-01", "1","love you forever"))
        timelineitemlist.add(TimelineItem("2015-01", "0","dismiss"))
        timelineitemlist.add(TimelineItem("2015-02", "0","love you forever"))
        timelineitemlist.add(TimelineItem("2015-01", "1","Interesting"))
        timelineitemlist.add(TimelineItem("2015-01", "1","what's wrong"))
        timelineitemlist.add(TimelineItem("2015-01", "0","who are you"))
        timelineitemlist.add(TimelineItem("2015-01", "1","Jillian"))
    }
}
