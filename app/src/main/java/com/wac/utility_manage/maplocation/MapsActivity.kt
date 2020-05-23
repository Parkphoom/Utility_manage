package com.wac.utility_manage.maplocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.utility_manage.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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
import me.samlss.broccoli.Broccoli
import me.samlss.broccoli.BroccoliGradientDrawable
import me.samlss.broccoli.PlaceholderParameter
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
    var txtcity: TextView? = null
    var txtaddress: TextView? = null
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

    private var broccoli: Broccoli? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        mContext = this
        setUI()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

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
        actionBar = supportActionBar
        Publiclayout().setActionBar(this.resources.getString(R.string.headerupdateuser), actionBar)

        broccoli = Broccoli()

        homeidlayout = findViewById(R.id.homeId_text_layout)
        homeididinput = findViewById(R.id.homeId_text_input)
        pubF.setOntextchange(this, homeididinput!!, homeidlayout!!)
        submitbtn = findViewById(R.id.nextbtn)
        submitbtn!!.setOnClickListener(this)
        txtaddress = findViewById(R.id.txtAddress)
        txtcity = findViewById(R.id.txtcity)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.searchmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
        return if (id == R.id.menu_search) {
            openAutocompleteActivity()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "OnMapReady")
        mMap = googleMap
        mMap!!.setOnCameraChangeListener { cameraPosition ->
            Log.d("Camera postion change" + "", cameraPosition.toString() + "")
            mCenterLatLong = cameraPosition.target
            Log.d("locate" + "", googleMap.cameraPosition.toString())
            Log.d("locate" + "", googleMap.uiSettings.toString())
            mMap!!.clear()
            try {

                val mLocation = Location("")
                mLocation.latitude = mCenterLatLong?.latitude!!
                mLocation.longitude = mCenterLatLong?.longitude!!
                startIntentService(mLocation)
                broccoli!!.removeAllPlaceholders()
                lat = mCenterLatLong!!.latitude.toString()
                lng = mCenterLatLong!!.longitude.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mMap!!.setOnCameraMoveStartedListener({
            Log.d("Camera move", googleMap.cameraPosition.toString())
            broccoli!!.addPlaceholder(
                PlaceholderParameter.Builder()
                    .setView(txtaddress)
                    .setDrawable(
                        BroccoliGradientDrawable(
                            Color.parseColor("#DDDDDD"),
                            Color.parseColor("#CCCCCC"), 20F, 800, LinearInterpolator()
                        )
                    )
                    .build()
            )
            broccoli!!.addPlaceholder(
                PlaceholderParameter.Builder()
                    .setView(txtcity)
                    .setDrawable(
                        BroccoliGradientDrawable(
                            Color.parseColor("#DDDDDD"),
                            Color.parseColor("#CCCCCC"), 20F, 800, LinearInterpolator()
                        )
                    )
                    .build()
            )
            broccoli!!.show()

            Log.d("broco", broccoli.toString())
        })


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
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
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
            broccoli!!.removeAllPlaceholders()
            Log.d("locate", "Changed")
            location.let { changeMap(it) }
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
            if (Places.isInitialized()) {
                Places.deinitialize()
            }
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
        broccoli!!.removeAllPlaceholders()
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
            latLong = LatLng(location.latitude, location.longitude)
            lat = location.latitude.toString()
            lng = location.longitude.toString()
            val cameraPosition =
                CameraPosition.Builder().target(latLong).zoom(19f).tilt(70f).build()
            Log.d("cameraPosition", "1_$location.latitude")

            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            Log.d("cameraPosition", "2_$location.longitude")
//            mLocationMarkerText!!.text = "Lat : " + location.latitude + "," + "Long : " + location.longitude


            startIntentService(location)
        } else {
            Toast.makeText(
                applicationContext,
                "Sorry! unable to create maps", Toast.LENGTH_SHORT
            ).show()
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
        @SuppressLint("SetTextI18n")
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY)
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA)
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY)
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET)
            Log.d("locate" + "", mAreaOutput)
            Log.d("locate" + "", mStateOutput)


            if (txtcity!!.visibility == View.GONE) {
                txtcity!!.visibility = View.VISIBLE
            }
            if (txtaddress!!.visibility == View.GONE) {
                txtaddress!!.visibility = View.VISIBLE
            }
            txtcity!!.text = mAreaOutput
            txtaddress!!.text = mStateOutput
            task.run()

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


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("_requestCode", requestCode.toString())
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                val place = PlaceAutocomplete.getPlace(mContext, data)

                // TODO call location based filter
                val latLong: LatLng
                latLong = place.latLng

                val cameraPosition =
                    CameraPosition.Builder().target(latLong).zoom(19f).tilt(70f).build()
                Log.d("cameraPosition", "3")
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
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                Log.d("cameraPosition", "4")
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Places.deinitialize()
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.d("address", place.name)
                Log.d("address", place.latLng?.latitude?.toString())
                Log.d("address", place.latLng?.longitude?.toString())
                try {
                    val mLocation = Location("")
                    mLocation.latitude = place.latLng!!.latitude
                    mLocation.longitude = place.latLng!!.longitude
                    startIntentService(mLocation)


                    Log.d("cameraPosition", "5_${place.latLng!!.latitude}")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val cameraPosition =
                    CameraPosition.Builder().target(place.latLng).zoom(19f).tilt(70f).build()
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                mMap!!.isMyLocationEnabled = true
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                Log.d("cameraPosition", "6_${place.latLng!!.longitude}")

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.d("address", status.statusMessage)
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            val status = PlaceAutocomplete.getStatus(mContext, data)
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
                    Log.d("res_PostGps", operate.toString())
                    val data = value.getString("data")
                    Log.d("res_PostGps", data.toString())

                    pubF.message(data.toString(), FancyToast.SUCCESS, this@MapsActivity)
                    homeididinput?.setText("")
                    homeid = ""
                }

                override fun onSucess(value: JSONArray) {

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

                override fun onSucess(value: JSONArray) {

                }

                override fun onFailure() {}
            })
    }

    private fun checkisempty(): Boolean {

        if (homeididinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (lat.isNullOrEmpty() || lng.isNullOrEmpty()) {
            pubF.message("ไม่สามารถรับค่าพิกัดได้", FancyToast.ERROR, this)
            return false
        } else {
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

    var AUTOCOMPLETE_REQUEST_CODE = 11
    private fun openAutocompleteActivity() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
            var fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            var intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private val task = Runnable { removeplace() }

    private fun removeplace() {
        broccoli!!.removeAllPlaceholders()
    }
}