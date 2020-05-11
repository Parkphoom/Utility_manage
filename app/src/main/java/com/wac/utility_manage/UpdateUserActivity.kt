package com.wac.utility_manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.Layer
import com.smartlib.addresspicker.AddressPickerActivity.Companion.RESULT_ADDRESS
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.PublicValues
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallback
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import org.json.JSONObject


class UpdateUserActivity : AppCompatActivity(), View.OnClickListener
     {

    private val mMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    private val TAG = "MAP LOCATION"
    var mContext: Context? = null
    var mLocationMarkerText: TextView? = null
    private var mCenterLatLong: LatLng? = null
    protected var mAddressOutput: String? = null
    protected var mAreaOutput: String? = null
    protected var mCityOutput: String? = null
    protected var mStateOutput: String? = null


    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataGPS = GpsObj()
    private var findMeterData = findMeterWaterData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion

    private val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var selectLocationButton: Button? = null
    private var permissionsManager: PermissionsManager? = null
    private var hoveringMarker: ImageView? = null
    private var droppedMarkerLayer: Layer? = null
    private var homeididinput: TextInputEditText? = null
    private var homeidlayout: TextInputLayout? = null
    private var submitbtn: Button? = null
    var building: FillLayer? = null

    private var lat:String? = ""
    private var lng:String? = ""
    private var homeid:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_updateuser)
        setContentView(R.layout.activity_updateuser)

        setUI()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PublicValues().ADDRESS_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val address: Address? = data?.getParcelableExtra(RESULT_ADDRESS) as Address
            Log.d("locationn", address?.latitude.toString())
            Log.d("locationn", address?.longitude.toString())
//            selected_address.text = address?.featureName + ", " + address?.locality + ", " + address?.adminArea + ", " + address?.countryName

        }
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun checkisempty(): Boolean {

        if (homeididinput!!.text.toString().isEmpty()) {
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
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

    fun PostGps(
        datapgps: GpsObj,
        id: String
    ) {
        retrofitCallfuntion.postGPSUpdate(this@UpdateUserActivity, datapgps,id,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val operate = value.getString("operate")
                    Log.d("res_postGPSUpdate", operate.toString())
                    val data = value.getString("data")
                    Log.d("res_postGPSUpdate", data.toString())



                }

                override fun onFailure() {
//                    Log.d(getString(R.string.LogError), e.message.toString())
                }
            })
    }

    fun Callfinduser(findMeterData: findMeterWaterData) {
        retrofitCallfuntion.findMeterUser(this@UpdateUserActivity, findMeterData,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getJSONObject("home").getJSONObject("data")
                    Log.d("res_Callfinduser", data.toString())
                    val user = value.getJSONObject("home").getJSONArray("user").getJSONObject(0)
                    Log.d("res_Callfinduser", user.toString())

                    dataGPS.latitude = lat
                    dataGPS.longitude = lng
                    PostGps(dataGPS,value.getString("_id"))
                }

                override fun onFailure() {}
            })
    }





}

