package com.wac.utility_manage.maplocation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.utility_manage.R
import com.google.android.gms.common.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallback
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import org.json.JSONObject

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener, View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataGPS = GpsObj()
    private var findMeterData = findMeterWaterData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    private var mMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    var mContext: Context? = null
    var mLocationMarkerText: TextView? = null
    private var mCenterLatLong: LatLng? = null

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private var mResultReceiver: AddressResultReceiver? = null

    private var homeididinput: TextInputEditText? = null
    private var homeidlayout: TextInputLayout? = null
    private var submitbtn: Button? = null

    protected var mAddressOutput: String? = null
    protected var mAreaOutput: String? = null
    protected var mCityOutput: String? = null
    protected var mStateOutput: String? = null
    private var homeid: String? = ""
    private var lat: String? = ""
    private var lng: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mContext = this
        setUI()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mLocationMarkerText = findViewById<View>(R.id.locationMarkertext) as TextView
        mapFragment!!.getMapAsync(this)
        mResultReceiver = AddressResultReceiver(Handler())
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                val dialog =
                    AlertDialog.Builder(mContext)
                dialog.setMessage("Location not enabled!")
                dialog.setPositiveButton(
                    "Open location settings"
                ) { paramDialogInterface, paramInt ->
                    val myIntent =
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                dialog.setNegativeButton("Cancel") { paramDialogInterface, paramInt ->
                    // TODO Auto-generated method stub
                }
                dialog.show()
            }
            buildGoogleApiClient()
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setUI() {
        pubF = Publicfunction()
        prtF = Printfuntion()
        retrofitCallfuntion = retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerupdateuser), actionBar)

        homeidlayout = findViewById(R.id.homeId_text_layout)
        homeididinput = findViewById(R.id.homeId_text_input)
        pubF.setOntextchange(this, homeididinput!!, homeidlayout!!)
        submitbtn = findViewById(R.id.nextbtn)
        submitbtn!!.setOnClickListener(this)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "OnMapReady")
        mMap = googleMap
        mMap!!.setOnCameraChangeListener { cameraPosition ->
            Log.d("Camera postion change" + "", cameraPosition.toString() + "")
            mCenterLatLong = cameraPosition.target
            mMap!!.clear()
            try {
                val mLocation = Location("")
                mLocation.latitude = mCenterLatLong?.latitude!!
                mLocation.longitude = mCenterLatLong?.longitude!!
                startIntentService(mLocation)
                mLocationMarkerText!!.text =
                    "Lat : " + mCenterLatLong!!.latitude + "," + "Long : " + mCenterLatLong!!.longitude
                lat = mCenterLatLong!!.latitude.toString()
                lng = mCenterLatLong!!.longitude.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val mLastLocation =
            LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient
            )
        if (mLastLocation != null) {
            changeMap(mLastLocation)
            Log.d(TAG, "ON connected")
        } else try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "Connection suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        try {
            location?.let { changeMap(it) }
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    override fun onStart() {
        super.onStart()
        try {
            mGoogleApiClient!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            mGoogleApiClient!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }


    override fun onPause() {
        super.onPause()
        try {
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    private fun checkPlayServices(): Boolean {
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                    resultCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                ).show()
            } else {
                //finish();
            }
            return false
        }
        return true
    }

    private fun changeMap(location: Location) {
        Log.d(TAG, "Reaching map$mMap")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap!!.uiSettings.isZoomControlsEnabled = false
            val latLong: LatLng
            latLong = LatLng(
                location.latitude,
                location.longitude
            )
            val cameraPosition =
                CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70f).build()
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.animateCamera(
                CameraUpdateFactory
                    .newCameraPosition(cameraPosition)
            )
            mLocationMarkerText!!.text =
                "Lat : " + location.latitude + "," + "Long : " + location.longitude
            startIntentService(location)
        } else {
            Toast.makeText(
                applicationContext,
                "Sorry! unable to create maps", Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    internal inner class AddressResultReceiver(handler: Handler?) :
        ResultReceiver(handler) {
        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY)
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA)
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY)
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET)


            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected fun startIntentService(mLocation: Location?) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(this, FetchAddressIntentService::class.java)

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver)

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation)

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent)
    }

    private fun openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                .build(this)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(
                this, e.connectionStatusCode,
                0 /* requestCode */
            ).show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            val message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode)
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                val place = PlaceAutocomplete.getPlace(mContext, data)

                // TODO call location based filter
                val latLong: LatLng
                latLong = place.latLng

                //mLocationText.setText(place.getName() + "");
                val cameraPosition =
                    CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70f).build()
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap!!.isMyLocationEnabled = true
                mMap!!.animateCamera(
                    CameraUpdateFactory
                        .newCameraPosition(cameraPosition)
                )
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            val status =
                PlaceAutocomplete.getStatus(mContext, data)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val TAG = "MAP LOCATION"
        private const val REQUEST_CODE_AUTOCOMPLETE = 1
    }


    fun PostGps(
        datapgps: GpsObj,
        id: String
    ) {
        retrofitCallfuntion.postGPSUpdate(this@MapsActivity, datapgps, id,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val operate = value.getString("operate")
                    Log.d("res_PostPayment", operate.toString())
                    val data = value.getString("data")
                    Log.d("res_PostPayment", data.toString())

                    pubF.message(data.toString(), FancyToast.SUCCESS,this@MapsActivity)
                    homeididinput?.setText("")
                    homeid = ""
                }

                override fun onFailure() {
//                    Log.d(getString(R.string.LogError), e.message.toString())
                }
            })
    }

    fun Callfinduser(findMeterData: findMeterWaterData) {
        retrofitCallfuntion.findMeterUser(this@MapsActivity, findMeterData,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getJSONObject("home").getJSONObject("data")
                    Log.d("res_Callfinduser", data.toString())
                    val user = value.getJSONObject("home").getJSONArray("user").getJSONObject(0)
                    Log.d("res_Callfinduser", user.toString())

                    dataGPS.latitude = lat
                    dataGPS.longitude = lng
                    PostGps(dataGPS, value.getString("_id"))
                }

                override fun onFailure() {}
            })
    }

    private fun checkisempty(): Boolean {

        if (homeididinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if(lat.isNullOrEmpty() || lng.isNullOrEmpty()){
            pubF.message("ไม่สามารถรับค่าพิกัดได้",FancyToast.ERROR,this)
            return false
        }
        else {
            homeidlayout!!.isErrorEnabled = false

//            homeid = homeidinput!!.text.toString()
//            meterid = meteridinput!!.text.toString()
            homeid = homeididinput!!.text.toString()

            return true
        }
    }

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (v?.id) {
            R.id.nextbtn -> {
                if (checkisempty()) {
                    findMeterData.address = homeid
                    findMeterData.meterid = ""
                    Callfinduser(findMeterData)

                }
            }
        }
    }
}