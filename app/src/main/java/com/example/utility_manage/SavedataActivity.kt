package com.example.utility_manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.example.utility_manage.Retrofit.Data.saveinfoData
import com.example.utility_manage.Retrofit.Jsobj
import com.example.utility_manage.Retrofit.retrofitUpload
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shivtechs.maplocationpicker.LocationPickerActivity
import com.shivtechs.maplocationpicker.MapUtility
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class SavedataActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var retrofitUpload: retrofitUpload
    private var infodata = saveinfoData()

    private var gpsInput: TextInputEditText? = null
    private var gpslayout: TextInputLayout? = null
    private var detailinput: TextInputEditText? = null
    private var detaillayout: TextInputLayout? = null

    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private var imgbtn: Button? = null
    private var savedatabtn: Button? = null
    private var img: ImageView? = null
    private var photoFile: File? = null
    private var infochoicechip: ChipGroup? = null
    private var lat: String = ""
    private var long: String = ""
    private var topic: String = ""
    private var detail: String = ""

    var imgOfficial: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savedata)
        setUI()

    }


    private fun setUI() {
        pubF = Publicfunction()
        retrofitUpload = retrofitUpload()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.savedata), actionBar)

        img = findViewById(R.id.img)

        gpslayout = findViewById(R.id.gps_text_layout)
        gpsInput = findViewById(R.id.gps_text_input)
        gpsInput!!.setOnClickListener(this)
        pubF.setOntextchange(this, gpsInput!!, gpslayout!!)
        detaillayout = findViewById(R.id.detail_text_layout)
        detailinput = findViewById(R.id.detail_text_input)
        pubF.setOntextchange(this, detailinput!!, detaillayout!!)

        infochoicechip = findViewById(R.id.info_choice_chip_group)
        infochoicechip!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.travelchip -> {
//                    pubF.message(getString(R.string.travel), this)
                    topic = getString(R.string.travel)
                }
                R.id.servicechip -> {
//                    pubF.message(getString(R.string.service), this)
                    topic = getString(R.string.service)
                }
                R.id.businesschip -> {
//                    pubF.message(getString(R.string.business), this)
                    topic = getString(R.string.business)
                }
                R.id.estatechip -> {
//                    pubF.message(getString(R.string.estate), this)
                }
                else -> topic = ""
            }
        }

        savedatabtn = findViewById<Button>(R.id.savedatabtn)
        savedatabtn!!.setOnClickListener(this)

        Photohelper(this, img!!)

        val floatingActionButton =
            findViewById<View>(R.id.takeimgbtn) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            choosePhotoHelper!!.showChooser()
        }

    }

    public fun Photohelper(activity: Activity, view: ImageView) {
        choosePhotoHelper = ChoosePhotoHelper.with(activity)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(activity)
                    .load(it)
                    .apply(RequestOptions.placeholderOf(R.drawable.icons8_image_200px))
                    .into(view!!)

                //        creating request body for file
                val file = File(it)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                imgOfficial = MultipartBody.Part.createFormData("imageOfficial", file.name, requestFile)
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PublicValues().ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    val address = data.getStringExtra(MapUtility.ADDRESS)
                    val currentLatitude =
                        data.getDoubleExtra(MapUtility.LATITUDE, 0.0)
                    val currentLongitude =
                        data.getDoubleExtra(MapUtility.LONGITUDE, 0.0)
                    lat = currentLatitude.toString()
                    long = currentLongitude.toString()
//                    txtAddress.setText("Address: $address")
                    gpsInput!!.setText("Lat:$currentLatitude  Long:$currentLongitude")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    companion object {

        fun start(context: Context) {
            context.startActivity(
                Intent(context, SavedataActivity::class.java)
            )
        }
    }

//    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
////and ACCESS_FINE_LOCATION
//    @SuppressLint("MissingPermission")
//    fun obtieneLocalizacion(
//        activity: Activity,
//        fusedLocationClient: FusedLocationProviderClient,
//        gpsInput: TextInputEditText?
//    ) {
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                PublicValues().GPS_LOCATION_REQUEST
//            )
//
//        } else {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    gpsInput!!.setText("${location?.latitude} : ${location?.longitude}")
//                    lat = location?.latitude.toString()
//                    long = location?.longitude.toString()
//                } else {
//                    pubF.message("Location error", activity)
//                }
//            }
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.gps_text_input -> {
//                obtieneLocalizacion(this, fusedLocationClient, gpsInput)
                val intent = Intent(
                    this@SavedataActivity,
                    LocationPickerActivity::class.java
                )
                startActivityForResult(intent, PublicValues(). ADDRESS_PICKER_REQUEST)
            }
            R.id.savedatabtn -> {
                if (checkisempty()) {
                    val gpslocation = Jsobj()
                    gpslocation.latitude = lat
                    gpslocation.longitude = long

                    infodata.settopic(topic)
                    infodata.setdetail(detail)
                    infodata.setgps(gpslocation)
                    Log.d("imggg", imgOfficial.toString())
                    retrofitUpload.saveInfo(this, infodata,topic,imgOfficial)
                }
            }

        }
    }

    private fun checkisempty(): Boolean {

        if (gpsInput!!.text.toString().isEmpty()) {
            gpslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (detailinput!!.text.toString().isEmpty()) {
            detaillayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (topic.isEmpty()) {
            pubF.message("กรุณาเลือกหัวข้อ",this)
            return false
        }
        else {
            gpslayout!!.isErrorEnabled = false
            detaillayout!!.isErrorEnabled = false

            detail = detailinput!!.text.toString()
            return true
        }
    }

}
