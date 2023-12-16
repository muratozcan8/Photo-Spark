package com.muratozcan.instaclone.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable zoom controls
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isZoomControlsEnabled = true

        val recievedIntent = intent
        val lat = recievedIntent.getStringExtra("lat")
        val lng = recievedIntent.getStringExtra("lng")
        val username = recievedIntent.getStringExtra("username")

        // Add a marker in Sydney and move the camera
        val location = lat?.let { lng?.let { it1 -> LatLng(it.toDouble(), it1.toDouble()) } }
        location?.let { MarkerOptions().position(it).title(username) }
            ?.let { mMap.addMarker(it) }
        location?.let { CameraUpdateFactory.newLatLngZoom(it, 10f) }?.let { mMap.moveCamera(it)
        }
    }
}