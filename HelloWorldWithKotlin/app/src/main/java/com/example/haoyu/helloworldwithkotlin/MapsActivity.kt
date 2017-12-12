package com.example.haoyu.helloworldwithkotlin

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.Log.d
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
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class MapsActivity : FragmentActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener, IconCallback, View.OnClickListener{



    private var mMap: GoogleMap? = null
    private var mGoogleApiClient:GoogleApiClient?=null
    private var mLastLocation:Location?=null
    private var countdowntTextView: TextView? = null
    private var recyclerview:RecyclerView? =null
    private var songParser: SongParser? =null
    private var lyricHashMap : HashMap<String, String>?=null
    private var bubble:Magnet?=null
    private var song:Song?=null
    private var user :String?=null
    private var usrdirpath: String?=null
    private var sfileManager :SFileManager?=null
    private var countdownTimer:ChallengeTimer?=null
    private var degree:String?=null
    private var mode:String?=null
    private var state = 0
    private var secondsLeft=0L
    private val markersHashMap = hashMapOf<Marker, marker>()
    private val markersObj = mutableListOf<Marker>()
    private val obtainedInterestingWord = mutableListOf<Word>()
    private val obtainedVeryInterestingWord = mutableListOf<Word>()
    private val obtainedBoringWord = mutableListOf<Word>()
    private val obtainedNotBoringWord = mutableListOf<Word>()
    private val obtainedUnclassifiedWord = mutableListOf<Word>()



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

        // initialize profile bubble
        val iconview = ImageView(this)
        iconview.setImageResource(R.mipmap.koyomi_circle)
        bubble = Magnet.newBuilder(this).setIconView(iconview)
                .setIconWidth(220)
                .setIconHeight(220)
                .setIconCallback(this)
                .setHideFactor(0.2f)
                .setShouldShowRemoveView(true)
                .setRemoveIconShouldBeResponsive(true)
                .setRemoveIconResId(R.mipmap.close)
                .setShouldStickToWall(true)
                .setInitialPosition(300,400)
                .build()

        // obtain instances of views on bottom sheet
        val bottomSheet = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        countdowntTextView = find(R.id.countdown_text)
        recyclerview = find(R.id.bottom_sheet_recyclerview)
        val linearlayoutManager = LinearLayoutManager(this)
        val btn = find<Button>(R.id.confirm_button)
        btn.setOnClickListener(this)

        // obtain values(mode and degree) from previous activity
        val intent = intent
        mode = intent.getStringExtra("mode")
        degree = intent.getStringExtra("degree")

        // initialize SFileManager
        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        user = pref.getString("username", "admin")
        usrdirpath = File(Environment.getExternalStorageDirectory().absolutePath, user).absolutePath
        sfileManager = SFileManager(user!!)

        // initialize SongParser
        val songNum =pref.getInt("songNum", 1)
        val randNum = (1..songNum).random()
        songParser = if(degree == "Hard")
            SongParser(randNum, 4)
        else
            SongParser(randNum, 5)
        song = songParser!!.song
        Log.d("tvSongName>>>>>>>", "${song!!.Title}")
        lyricHashMap = songParser!!.lyricsmap

        // Set ClickListener for CountDown TextView. This TextView is clickable once
        countdowntTextView!!.setOnClickListener {
            if(state==0) {
                state = 1
                // Select markers that will be added on the map
                val markersInMapList = markerGenerator(songParser!!.loadMarkerList(), degree!!)

                // Add selected markers to the map
                markersInMapList.forEach { v ->
                    val markerObj = mMap!!.addMarker(MarkerOptions()
                            .position(LatLng(v.latitude.toDouble(), v.longitude.toDouble())))
                    markersObj.add(markerObj)
                    markersHashMap.put(markerObj, v)

                    // Set icon for different styles of markers
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
                bubble!!.show()

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

    override fun onClick(v: View?) {
        d("btnid", "${v!!.id} ${R.id.confirm_button}")
        when(v.id){
            R.id.confirm_button ->{
                // handle the click event of confirm button
                val editText = find<EditText>(R.id.answer_edittext)
                if(editText.text.toString().toLowerCase() == song!!.Title.toLowerCase()){
                    onSuccess()
                }else
                    snackbar(editText, "Incorrect. Try again")
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        // Calculate the distance from current location to marker's location
        val markerLocation = Location("marker")
        markerLocation.latitude = p0!!.position.latitude
        markerLocation.longitude = p0.position.longitude
        val distance = markerLocation.distanceTo(mLastLocation)

        if(distance<15 && p0 in markersHashMap){
            // handle non-bonus marker
            // Obtain the word corresponding to the marker
            val word = lyricHashMap!![markersHashMap[p0]!!.name]
            val style = markersHashMap[p0]!!.style
            Log.d("collect marker", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>$word")

            // Classify the word to Expandable RecyclerView
            when(style){
                "boring" -> obtainedBoringWord.add(Word(word!!))
                "notboring" -> obtainedNotBoringWord.add(Word(word!!))
                "interesting"-> obtainedInterestingWord.add(Word(word!!))
                "veryinteresting" -> obtainedVeryInterestingWord.add(Word(word!!))
                else -> obtainedUnclassifiedWord.add(Word(word!!))
            }
            val adapter = TypeAdapter(constructData())
            recyclerview!!.adapter=adapter
            toast("obtained word: $word")

            // Remove collected the marker
            p0.remove()
        }
        else if(distance<15 && p0 !in markersHashMap){
            /**
             * handle bonus marker
             * Cancel original CountDown Timer. Calculate bonus time and start a new timer.
             *
             * for Easy degree, bonus time is in the range of 30~60 seconds
             * for Moderate degree, bonus time is in the range of 20~60 seconds
             * for Hard degree, bonus time is in the range of 10~60 seconds
             */
            countdownTimer!!.cancel()
            val i = when(degree){
                "Easy" -> Random().nextInt(30)+30
                "Moderate" -> Random().nextInt(40)+20
                else -> Random().nextInt(50)+10
            }
            d("time added", "$i")
            startTimer((secondsLeft+i)*1000, 1000)

            // Remove collected bonus marker
            p0.remove()
        }
        else
            toast("get closer!")
        return true
    }


    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // Bubble's override functions start
    override fun onFlingAway() {
        onFail()
    }

    override fun onMove(p0: Float, p1: Float) {

    }

    override fun onIconDestroyed() {
        finish()
    }

    override fun onIconClick(p0: View?, p1: Float, p2: Float) {

    }
    // Bubble's override functions end
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    override fun onStart() {
        mGoogleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        mGoogleApiClient!!.disconnect()
        super.onStop()
    }

    override fun onBackPressed() {
        if(state == 1)
        // the game has started
            onFail()
        else
        // the game has not started yet
            super.onBackPressed()

    }

    //mMap
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {

            enableMyLocation()
        } catch (se: SecurityException) {
            Log.d("map my location", "security exception thrown")
        }
        mMap!!.setPadding(0, 10, 0, 0)
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true
    }



    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // Connection-related override functions start
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
            // Update my LastLocation
            mLastLocation=p0
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("mapactivity", ">>>>>onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("mapactivity", ">>>onConnectedFailed")

    }
    // Connection-related override functions end
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    /**
     * The method randomly picks markers in certain kml file. The number of markers picked is
     * according to the degree of difficulty in this game. Generally,
     * in Easy degree, 50% of markers will be picked
     * in Moderate degree, 30% of markers will be picked
     * in Hard degree, 20% of markers will be picked
     *
     * @param allmarkerList a list of all the markers in a certain kml file
     * @param degree the degree of difficulty in this game
     *
     * @return a list of markers which will be used in this game
     */
    private fun markerGenerator(allmarkerList : MutableList<marker>, degree: String):MutableList<marker>{

        val markersInMapList = mutableListOf<marker>()

        when (degree) {
            "Easy" -> {
                allmarkerList.forEach { v ->
                    if(Random().nextDouble()>0.50)
                        markersInMapList.add(v)
                }
                return markersInMapList
            }
            "Moderate" -> {

                allmarkerList.forEach { v->
                    if(Random().nextDouble()>0.7)
                        markersInMapList.add(v)
                }
                return markersInMapList
            }
            else -> {
                allmarkerList.forEach { v->
                    if(Random().nextDouble()>0.8)
                        markersInMapList.add(v)
                }
                return markersInMapList
            }
        }
    }

    /**
     * This method randomly generates a bonus marker within the regulated range,
     * and mark notification for the player after the marker added to the map.
     */
    private fun addBonusMarker(){
        val latitude = Random().nextDouble()*(55.946233-55.942617)+55.942617
        val longitude = Random().nextDouble()*(-3.184319+3.192473)-3.192473
        d("bonus marker","$latitude, $longitude")
        mMap!!.addMarker(MarkerOptions()
                .position(LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bonus)))
        longSnackbar(recyclerview!!, "Bonus marker appears")
        mMap!!.setOnMarkerClickListener(this)
    }

    // Start the timers shows on the Bottom Sheet
    private fun startTimer(duration: Long, interval: Long){
        countdownTimer = ChallengeTimer(duration, interval)
        countdownTimer!!.start()
    }

    // This method will be called when the game is failed or the player intends to exit the game.
    private fun onFail(){

        alert("This challenge failed. Try again!", "Challenge Failed") {
            yesButton {
                // Add the failed record to fail and exit the game.
                val time = DateFormat.getDateTimeInstance().format(Date())
                val result = "0"
                val song = song!!.Title
                sfileManager!!.updateTI(TimelineItem(time, result, song))
                bubble!!.destroy()
            }
            noButton {
                if (secondsLeft <= 1L) {
                    // Add the failed record to fail and exit the game.
                    val time = DateFormat.getDateTimeInstance().format(Date())
                    val result = "0"
                    val song = song!!.Title
                    sfileManager!!.updateTI(TimelineItem(time, result, song))
                    bubble!!.destroy()
                }
            }
        }.show().setCancelable(false)


    }

    // This method will be called when the game is passed
    private fun onSuccess(){
        // Add record to the file
        val time = DateFormat.getDateTimeInstance().format(Date())
        val result = "1"
        val name = this.song!!.Title
        sfileManager!!.updateTI(TimelineItem(time, result, name))
        if(mode=="Challenge")
            sfileManager!!.updateUSL(this.song!!)

        // Exit the game
        alert("The challenge succeeded. Congratulations", "Challenge Succeeded") {
            yesButton { bubble!!.destroy()}
            noButton { bubble!!.destroy()}
        }.show().setCancelable(false)


    }


    private fun enableMyLocation()=
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            else mMap!!.isMyLocationEnabled = true

    private fun createLocationRequest(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    // Randomly generate a index of song which is never guessed correctly before.
    private fun ClosedRange<Int>.random():Int {
        val manager = SFileManager(user!!)
        while(true) {
            val num = Random().nextInt(endInclusive - start) + start
            if(manager.isSongAvailable(num))
                return num
        }

    }

    // Construct data for Expandable RecyclerView
    private fun constructData(): List<Type> =
            Arrays.asList(Type("boring", obtainedBoringWord), Type("notboring", obtainedNotBoringWord),
                    Type("interesting", obtainedInterestingWord), Type("veryinteresting", obtainedVeryInterestingWord),
                    Type("unclassified", obtainedUnclassifiedWord))

    /**
     * The inner class monitors any network changes and notifies the player if network is unavailable
     */
    private inner class NetworkChangeReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if(networkInfo==null || !networkInfo.isAvailable){
                longSnackbar(find<RecyclerView>(R.id.countdown_text), "network unavailable, please check your network")
            }
        }
    }

    /**
     * The inner class starts a new countdown timer and changes the countdown textview synchronously.
     */
    private inner class ChallengeTimer(duration: Long, interval: Long) : CountDownTimer(duration, interval) {
        override fun onFinish() {
            onBackPressed()
        }

        override fun onTick(millisUntilFinished: Long) {
            secondsLeft = millisUntilFinished / 1000
            val minutesLeft = secondsLeft / 60
            secondsLeft -= minutesLeft * 60
            countdowntTextView!!.text = "$minutesLeft : $secondsLeft"
            secondsLeft += minutesLeft * 60
            if (secondsLeft % 30 == 0L && Random().nextDouble() < 0.25)
                addBonusMarker()
        }
    }
}


