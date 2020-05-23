package com.example.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var gpsInput: TextInputEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUI()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.gps_text_input -> {
                obtieneLocalizacion(this, fusedLocationClient, gpsInput)
            }
        }
    }

    private fun setUI() {
        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerregister), actionBar)

        gpsInput = findViewById(R.id.gps_text_input)
        gpsInput!!.setOnClickListener(this)
    }


    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    fun obtieneLocalizacion(
        activity: Activity,
        fusedLocationClient: FusedLocationProviderClient,
        gpsInput: TextInputEditText?
    ) {
        var a : Array<String> = arrayOf("")
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PublicValues().GPS_LOCATION_REQUEST
            )

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    gpsInput!!.setText("${location?.latitude} : ${location?.longitude}")
                    a = arrayOf(location?.longitude.toString(),location?.latitude.toString())
                } else {
//                    message("Location error", activity)
//                    statusCheck(activity)
                }
            }
        }
    }



}
