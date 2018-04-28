package com.example.hackintosh.sprayart

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                              Manifest.permission.CAMERA)

    lateinit var location: Location

    lateinit var cameraButton: Button
    lateinit var photoLocation: TextView
    lateinit var currentLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for(permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
                break
            }
        }

        photoLocation = findViewById(R.id.photoLocation)
        currentLocation = findViewById(R.id.currentLocation)

//        // Acquire a reference to the system Location Manager
//        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        // Define a listener that responds to location updates
//        val locationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                // Called when a new location is found by the network location provider.
//                this@MainActivity.location = location
//                currentLocation.text = "${location.latitude} / ${location.longitude}"
//            }
//
//            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//
//            override fun onProviderEnabled(provider: String) {}
//
//            override fun onProviderDisabled(provider: String) {}
//        }
//
//        // Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)

        cameraButton = findViewById(R.id.cameraButton)

        cameraButton.setOnClickListener({
            //            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            startActivityForResult(intent, 10)
            val intent = Intent(this, SprayArt::class.java)
            startActivity(intent)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == 10) {
            photoLocation.text = "${location.latitude}  / ${location.longitude}"
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
