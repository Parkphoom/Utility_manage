package com.wac.utility_manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.GpsObj
import com.wac.utility_manage.Retrofit.Data.saveinfoData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


class SavedataActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var retrofitCallfuntion: retrofitCallfuntion
    private var infodata = saveinfoData()

    private var detailinput: TextInputEditText? = null
    private var detaillayout: TextInputLayout? = null

    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private var imgbtn: Button? = null
    private var savedatabtn: Button? = null
    private var img: ImageView? = null
    private var photoFile: File? = null
    private var topic: String = ""
    private var detail: String = ""

    var imgOfficial: MultipartBody.Part? = null
    var editTextFilledExposedDropdown: AutoCompleteTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savedata)
        setUI()

        val typelist: MutableList<String> = ArrayList()
        typelist.add("ท่องเที่ยว")
        typelist.add("บริการ")
        typelist.add("ธุรกิจ")

        val typeDataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            typelist
        )

        editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown?.setAdapter(typeDataAdapter)


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

    override fun onStart() {
        super.onStart()

        pubF.obtieneLocalizacion(this, pubF.fusedLocationClient)
    }


    private fun setUI() {
        pubF = Publicfunction()
        retrofitCallfuntion = retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout()
            .setActionBar(this.resources.getString(R.string.savedata), actionBar)
        pubF.Slideleft(this)

        img = findViewById(R.id.img)
        detaillayout = findViewById(R.id.detail_text_layout)
        detailinput = findViewById(R.id.detail_text_input)
        pubF.setOntextchange(this, detailinput!!, detaillayout!!)


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
                if (it != null) {
                    val file = File(it)
                    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                    imgOfficial =
                        MultipartBody.Part.createFormData("imageOfficial", file.name, requestFile)
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper!!.onActivityResult(requestCode, resultCode, data)

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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }


    companion object {

        fun start(context: Context) {
            context.startActivity(
                Intent(context, SavedataActivity::class.java)
            )
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.savedatabtn -> {
                if (pubF.lat.isEmpty() || pubF.long.isEmpty()) {
                    pubF.Slideleft(this)
                }
                if (checkisempty()) {
                    val gpslocation =
                        GpsObj()
                    gpslocation.latitude = pubF.lat
                    gpslocation.longitude = pubF.long

                    infodata.settopic(topic)
                    infodata.setdetail(detail)
                    infodata.setgps(gpslocation)
                    Log.d("imggg", imgOfficial.toString())
                    retrofitCallfuntion.saveInfo(this, infodata, topic, imgOfficial)
                }
            }

        }
    }

    private fun checkisempty(): Boolean {


        if (detailinput!!.text.toString().isEmpty()) {
            detaillayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }

        if (editTextFilledExposedDropdown?.text.toString().trim().isEmpty()) {
            pubF.message("กรุณาเลือกหัวข้อ", FancyToast.CONFUSING, this)
            return false
        } else {
            detaillayout!!.isErrorEnabled = false
            topic = editTextFilledExposedDropdown?.text.toString()
            detail = detailinput!!.text.toString()
            return true
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        pubF.lat = ""
        pubF.long = ""
    }


}
