package com.example.haoyu.helloworldwithkotlin

import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.File


class MapsActivity : FragmentActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener{


    private var mMap: GoogleMap? = null
    private var mGoogleApiClient:GoogleApiClient?=null
    private var mLastLocation:Location?=null
    private var songxml = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/01/map1.kml")
    private var songtxt = File(Environment.getExternalStorageDirectory().absolutePath + "/songlist/01/words.txt")



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

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //mMap!!.setOnMyLocationButtonClickListener(this)
        val list = SongParser(1, 1).markerList
        for (marker in list){
            mMap!!.addMarker(MarkerOptions()
                .position(LatLng(marker.latitude.toDouble(), marker.longitude.toDouble()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.boring))) }
        try {

            enableMyLocation()
        } catch (se: SecurityException) {
            Log.d("map my location", "security exception thrown")
        }
        mMap!!.setPadding(0, 10, 0, 0)
        mMap!!.uiSettings.isMyLocationButtonEnabled = true

    }


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


