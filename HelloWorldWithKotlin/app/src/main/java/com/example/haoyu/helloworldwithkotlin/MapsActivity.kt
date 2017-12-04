package com.example.haoyu.helloworldwithkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.premnirmal.Magnet.IconCallback
import com.premnirmal.Magnet.Magnet
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.HashMap


class MapsActivity : FragmentActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener, IconCallback{



    private var mMap: GoogleMap? = null
    private var mGoogleApiClient:GoogleApiClient?=null
    private var mLastLocation:Location?=null
    private var countdowntTextView: TextView? = null
    private var recyclerview:RecyclerView? =null
    private var songParser: SongParser? =null
    private var lyricHashMap : HashMap<String, String>?=null
    private var magnet:Magnet?=null
    private val markersHashMap = hashMapOf<Marker, marker>()
    private val markersObj = mutableListOf<Marker>()
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

        //profile bubble
        val iconview = ImageView(this)
        iconview.setImageResource(R.mipmap.koyomi_circle)
        magnet = Magnet.newBuilder(this).setIconView(iconview)
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

        //views on bottom sheet
        val bottomSheet = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        countdowntTextView = find(R.id.countdown_text)
        recyclerview = find(R.id.bottom_sheet_recyclerview)
        val linearlayoutManager = LinearLayoutManager(this)


        //values from previous activity
        val intent = intent
        val mode = intent.getStringExtra("mode")
        val degree = intent.getStringExtra("degree")


        //init SongParser
        if(degree == "Hard")
            songParser = SongParser((1..18).random(), 4)
        else
            songParser = SongParser((1..18).random(), 5)
        lyricHashMap = songParser!!.lyricsmap


        var count = 0
        countdowntTextView!!.setOnClickListener {
            if(count==0) {
                count++
                val markersInMapList = markerGenerator(songParser!!.loadMarkerList(), degree)
                markersInMapList.forEach { v ->
                    val markerObj = mMap!!.addMarker(MarkerOptions()
                            .position(LatLng(v.latitude.toDouble(), v.longitude.toDouble())))
                    markersObj.add(markerObj)
                    markersHashMap.put(markerObj, v)
                    when (v.style) {
                        "boring" ->
                            markersObj[markersObj.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.boring))
                        "notboring" ->
                            markersObj[markersObj.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.notboring))
                        "interesting" ->
                            markersObj[markersObj.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.interesting))
                        "veryinteresting" ->
                            markersObj[markersObj.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.veryinteresting))
                        else ->
                            markersObj[markersObj.size - 1].setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.unclassified))
                    }
                }
                mMap!!.setOnMarkerClickListener(this)
                magnet!!.show()

                //init Expandable RecyclerView
                val adapter = TypeAdapter(constructData())
                recyclerview!!.layoutManager=linearlayoutManager
                recyclerview!!.adapter=adapter
                bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED


                if(mode=="Challenge")
                    startTimer(60000*6, 1000)
                if(mode=="Casual")
                    countdowntTextView!!.text = "drag up to review gained markers"
            }
        }



    }





    override fun onMarkerClick(p0: Marker?): Boolean {

        val markerLocation = Location("marker")
        markerLocation.latitude = p0!!.position.latitude
        markerLocation.longitude = p0!!.position.longitude
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        val distance = markerLocation.distanceTo(mLastLocation)
        if(distance<5){
            val word = lyricHashMap!![markersHashMap[p0]!!.name]
            val style = markersHashMap[p0]!!.style
            when(style){
                "boring" -> obtained_boringword.add(Word(word!!))
                "notboring" ->obtained_notboringword.add(Word(word!!))
                "interesting"->obtained_interestingword.add(Word(word!!))
                "veryinteresting" ->obtained_interestingword.add(Word(word!!))
                else -> obtained_unclassifiedword.add(Word(word!!))
            }
            val adapter = TypeAdapter(constructData())
            recyclerview!!.adapter=adapter
            toast("obtained word: $word")
            p0.remove()
        }
        else{
            toast("get closer!!")
        }
        return true
    }

    fun markerGenerator(allmarkerList : MutableList<marker>, degree: String):MutableList<marker>{

        val markersInMapList = mutableListOf<marker>()

        if(degree == "Easy"){
            allmarkerList.forEach { v ->
                if(Random().nextDouble()>0.75)
                    markersInMapList.add(v)
            }
            return markersInMapList
        }
        else if(degree == "Moderate"){

            allmarkerList.forEach { v->
                if(Random().nextDouble()>0.8)
                    markersInMapList.add(v)
            }
            return markersInMapList
        }
        else{
            allmarkerList.forEach { v->
                if(Random().nextDouble()>0.85)
                    markersInMapList.add(v)
            }
            return markersInMapList
        }
    }


    fun ClosedRange<Int>.random()=
            Random().nextInt(endInclusive-start)+start


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
                var secondsLeft = millisUntilFinished / 1000
                val minutesLeft = secondsLeft / 60
                secondsLeft = secondsLeft - minutesLeft * 60
                countdowntTextView!!.setText(minutesLeft.toString()+" : "+secondsLeft.toString())
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
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnected(p0: Bundle?) {
        try{ createLocationRequest()}
        catch (ise: IllegalStateException){
            Log.d("Mapactivity", "IllegalStateException[onConnected]")
        }
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
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
        Log.d("mapactivity", ">>>>>onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("mapactivity", ">>>onConnectedFailed")
    }



}


