package com.example.haoyu.helloworldwithkotlin

import android.content.pm.PackageManager
import android.icu.util.DateInterval
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.premnirmal.Magnet.IconCallback
import com.premnirmal.Magnet.Magnet
import java.io.File
import java.util.*
import javax.xml.datatype.Duration


class MapsActivity : FragmentActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener, IconCallback {



    private var mMap: GoogleMap? = null
    private var mGoogleApiClient:GoogleApiClient?=null
    private var mLastLocation:Location?=null
    private var parser: SongParser? =null
    val markerlist = mutableListOf<Marker>()
    private var countdowntext: TextView? = null
    private val obtained_interestingword = mutableListOf<Word>()
    private val obtained_veryinterestingword = mutableListOf<Word>()
    private val obtained_boringword = mutableListOf<Word>()
    private val obtained_notboringword = mutableListOf<Word>()
    private val obtained_unclassifiedword = mutableListOf<Word>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        val recyclerview = findViewById(R.id.bottom_sheet_recyclerview) as RecyclerView
        val linearlayoutManager = LinearLayoutManager(this)
        val bottomSheet = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


        parser = SongParser(1, 5)
        countdowntext = findViewById(R.id.coutdown_text) as TextView
        val fab = findViewById(R.id.start_finsh_button) as FloatingActionButton
        var count = 0


        val iconview = ImageView(this)
        iconview.setImageResource(R.mipmap.koyomi_circle)
        val magnet = Magnet.newBuilder(this).setIconView(iconview)
                .setIconWidth(250)
                .setIconHeight(250)
                .setIconCallback(this)
                .setHideFactor(0.2f)
                .setShouldShowRemoveView(true)
                .setRemoveIconShouldBeResponsive(true)
                .setRemoveIconResId(R.mipmap.close)
                .setShouldStickToWall(true)
                .setInitialPosition(300,400)
                .build()



        fab.setOnClickListener { view ->
            if(count == 0){
                startTimer(60000*5, 1000)
                count = count + 1
                magnet.show()
                fab.hide()
                val adapter = TypeAdapter(constructData())
                recyclerview.layoutManager=linearlayoutManager
                recyclerview.adapter=adapter

                bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
            }
            else{
                val adapter = TypeAdapter(constructData())
                recyclerview.layoutManager=linearlayoutManager
                recyclerview.adapter=adapter

                bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onFlingAway() {

    }

    override fun onMove(p0: Float, p1: Float) {

    }

    override fun onIconDestroyed() {
        finish()
    }

    override fun onIconClick(p0: View?, p1: Float, p2: Float) {

    }

    private fun startTimer(duration: Long, interval: Long){
        val countdowntimer = object: CountDownTimer(duration, interval){
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                var secondsleft = millisUntilFinished / 1000
                val minutesleft = secondsleft / 60
                secondsleft = secondsleft - minutesleft * 60
                countdowntext!!.setText(minutesleft.toString()+" : "+secondsleft.toString())
            }

        }
        countdowntimer.start()
    }

    override fun onStart() {
        mGoogleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        mGoogleApiClient!!.disconnect()
        super.onStop()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val list = parser!!.loadMarkerList()
        //Log.d("MapActivity",parser.loadSongList().toString())
        val lyricmap = parser!!.lyricsmap

        for (marker in list) {
            if (Random().nextDouble() > 0.7) {
                markerlist.add(mMap!!.addMarker(MarkerOptions()
                        .position(LatLng(marker.latitude.toDouble(), marker.longitude.toDouble()))))
                when (marker.style) {
                    "boring" -> {markerlist[markerlist.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.boring))
                                obtained_boringword.add(Word(lyricmap[marker.name]!!))}
                    "notboring" -> {markerlist[markerlist.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.notboring))
                                obtained_notboringword.add(Word(lyricmap[marker.name]!!))}
                    "interesting" -> {markerlist[markerlist.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.interesting))
                                obtained_interestingword.add(Word(lyricmap[marker.name]!!))}
                    "veryinteresting" -> {markerlist[markerlist.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.veryinteresting))
                                obtained_veryinterestingword.add(Word(lyricmap[marker.name]!!))}
                    else -> {markerlist[markerlist.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.unclassified))
                                obtained_unclassifiedword.add(Word(lyricmap[marker.name]!!))}
                }
            }
        }
        mMap!!.addMarker(MarkerOptions()
                .position(LatLng(55.945896, -3.188891))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bonus)))



        try {

            enableMyLocation()
        } catch (se: SecurityException) {
            Log.d("map my location", "security exception thrown")
        }
        mMap!!.setPadding(0, 10, 0, 0)
        mMap!!.uiSettings.isMyLocationButtonEnabled = true

    }

    fun constructData(): List<Type> =
        Arrays.asList(Type("boring", obtained_boringword), Type("notboring", obtained_notboringword),
                Type("interesting", obtained_interestingword), Type("veryinteresting", obtained_veryinterestingword),
                Type("unclassified", obtained_unclassifiedword))



    private fun enableMyLocation()=
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else mMap!!.isMyLocationEnabled = true

    fun createLocationRequest(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnected(p0: Bundle?) {
        try{ createLocationRequest()}
        catch (ise: IllegalStateException){
            Log.d("Mapactivity", "IllegalStateException[onConnected]")
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
    }

    override fun onLocationChanged(p0: Location?) {
        if(p0 == null){
            Log.d("mapactivity", "[onLocationChanged] Location unknown")
        }else{
            //do something
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("mapactivity", ">>>>>onCOnnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("mapactivity", ">>>onConnectedFailed")
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        Toast.makeText(this, "marker clicked", Toast.LENGTH_SHORT).show()
        return true
    }

}


