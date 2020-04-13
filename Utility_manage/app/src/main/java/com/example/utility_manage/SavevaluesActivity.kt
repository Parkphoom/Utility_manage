package com.example.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.utility_manage.PublicAction.PublicValues
import com.example.utility_manage.PublicAction.Publicfunction
import com.example.utility_manage.PublicAction.Publiclayout
import com.example.utility_manage.Retrofit.Jsobj
import com.example.utility_manage.Retrofit.retrofitData
import com.example.utility_manage.Retrofit.retrofitUpload
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.telpo.tps550.api.printer.UsbThermalPrinter
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SavevaluesActivity : AppCompatActivity(), View.OnClickListener {
    private var pictureImagePath = ""
    private val Image_Capture_Code2 = 2

    private var homeid: String = ""
    private var meterid: String = ""
    private var valuesid: String = ""
    private var type: String = ""
    private var amount: String = ""
    private var lat: String = ""
    private var long: String = ""

    private var cambtn: ImageButton? = null
    private var imgcam: ImageView? = null
    private var gpsInput: TextInputEditText? = null
    private var gpslayout: TextInputLayout? = null
    private var homeidinput: TextInputEditText? = null
    private var homeidlayout: TextInputLayout? = null
    private var meteridinput: TextInputEditText? = null
    private var meteridlayout: TextInputLayout? = null
    private var valuesidinput: TextInputEditText? = null
    private var valuesidlayout: TextInputLayout? = null
    private var typepayidinput: TextInputEditText? = null
    private var typepayidlayout: TextInputLayout? = null
    private var amountinput: TextInputEditText? = null
    private var amountlayout: TextInputLayout? = null
    private var submitbtn: Button? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private val IMAGE_DIRECTORY_NAME = "WAC"
    private lateinit var mCurrentPhotoPath: String

    private lateinit var pubF: Publicfunction
    var dataInvoice = retrofitData()
    private lateinit var retrofitUpload: retrofitUpload

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savevalues)

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
            R.id.opencambtn -> {
//                openBackCamera()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureImage()
                } else {
                    captureImage2()

                }
            }
            R.id.gps_text_input -> {
                obtieneLocalizacion(this, fusedLocationClient, gpsInput)

            }
            R.id.submitbtn -> {
                if (checkisempty()) {

                    val gpslocation = Jsobj()
                    gpslocation.latitude = lat
                    gpslocation.longitude = long

                    dataInvoice.ref_1 = homeid
                    dataInvoice.ref_2 = ""
                    dataInvoice.setmeterID(meterid)
                    dataInvoice.setvaluesID(valuesid)
                    dataInvoice.category = type
                    dataInvoice.amount = amount
                    dataInvoice.setstartDate(pubF.getDatetimenow())
                    dataInvoice.setdueDate("")
                    dataInvoice.setgps(gpslocation)

                    retrofitUpload.upload(this, dataInvoice)

                }


            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
            Log.d("imggg",photoFile!!.getAbsolutePath())
            imgcam!!.setImageBitmap(myBitmap)
        } else {
            pubF.message("Request cancelled or something went wrong.", this)
        }
    }


    private fun setUI() {
        pubF = Publicfunction()
        pubF.handler = pubF.MyHandler(this)
        pubF.mUsbThermalPrinter = UsbThermalPrinter(this)
        retrofitUpload = retrofitUpload()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headersavevalues), actionBar)

        cambtn = findViewById(R.id.opencambtn)
        cambtn!!.setOnClickListener(this)
        submitbtn = findViewById(R.id.submitbtn)
        submitbtn!!.setOnClickListener(this)

        imgcam = findViewById(R.id.imgfromcam)
        imgcam!!.setImageDrawable(getResources().getDrawable(R.drawable.icons8_home_500px_1))

        gpslayout = findViewById(R.id.gps_text_layout)
        gpsInput = findViewById(R.id.gps_text_input)
        gpsInput!!.setOnClickListener(this)
        pubF.setOntextchange(this, gpsInput!!, gpslayout!!)
        homeidlayout = findViewById(R.id.homeid_text_layout)
        homeidlayout!!.helperText = "ตัวอย่าง : 01/001"
        homeidinput = findViewById(R.id.homeid_text_input)
        pubF.setOntextchange(this, homeidinput!!, homeidlayout!!)
        meteridlayout = findViewById(R.id.meterId_text_layout)
        meteridinput = findViewById(R.id.meterId_text_input)
        pubF.setOntextchange(this, meteridinput!!, meteridlayout!!)
        valuesidlayout = findViewById(R.id.valuesId_text_layout)
        valuesidinput = findViewById(R.id.valuesId_text_input)
        pubF.setOntextchange(this, valuesidinput!!, valuesidlayout!!)
        typepayidlayout = findViewById(R.id.typepay_text_layout)
        typepayidinput = findViewById(R.id.typepay_text_input)
        pubF.setOntextchange(this, typepayidinput!!, typepayidlayout!!)
        amountlayout = findViewById(R.id.amount_text_layout)
        amountinput = findViewById(R.id.amount_text_input)
        pubF.setOntextchange(this, amountinput!!, amountlayout!!)

    }

    private fun checkisempty(): Boolean {
        if (meteridinput!!.text.toString().isEmpty() && homeidinput!!.text.toString().isEmpty()) {
            meteridlayout!!.error = resources.getString(R.string.gettexterror)
            homeidlayout!!.error = resources.getString(R.string.gettexterror)
            pubF.message(getString(R.string.gettexthint), this)
            return false
        }
        if (gpsInput!!.text.toString().isEmpty()) {
            gpslayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (valuesidinput!!.text.toString().isEmpty()) {
            valuesidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (typepayidinput!!.text.toString().isEmpty()) {
            typepayidlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        }
        if (amountinput!!.text.toString().isEmpty()) {
            amountlayout!!.error = resources.getString(R.string.gettexterror)
            return false
        } else {
            homeidlayout!!.isErrorEnabled = false
            meteridlayout!!.isErrorEnabled = false
            gpslayout!!.isErrorEnabled = false
            valuesidlayout!!.isErrorEnabled = false
            typepayidlayout!!.isErrorEnabled = false
            amountlayout!!.isErrorEnabled = false

            homeid = homeidinput!!.text.toString()
            meterid = meteridinput!!.text.toString()
//            gpslocation = gpsInput!!.text.toString()
            valuesid = valuesidinput!!.text.toString()
            type = typepayidinput!!.text.toString()
            amount = amountinput!!.text.toString()

            return true
        }
    }


    private fun openBackCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "$timeStamp.jpeg"
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "IMAGE_DIRECTORY_NAME"
        )
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        //        File storageDir = new File("/storage/emulated/0/Pictures/");
        storageDir.mkdirs()
        pictureImagePath = storageDir.absolutePath + "/" + imageFileName
        val file = File(pictureImagePath)
//        val outputFileUri = Uri.fromFile(file)
        val outputFileUri: Uri = FileProvider.getUriForFile(
            this,
            this.getApplicationContext().getPackageName().toString() + ".provider",
            file
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, Image_Capture_Code2)

    }


    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private fun captureImage2() {

        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = createImageFile4()
            if (photoFile != null) {
                pubF.message(photoFile!!.getAbsolutePath(), this)
                Log.i("Mayank", photoFile!!.getAbsolutePath())
//                val photoURI = Uri.fromFile(photoFile)
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext().getPackageName().toString() + ".provider",
                    photoFile!!
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (e: Exception) {
            pubF.message("Camera is not available." + e.toString(), this)
        }

    }

    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create the File where the photo should go
            try {

                photoFile = createImageFile()
//                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
                Log.i("Mayank", photoFile!!.getAbsolutePath())

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("clickkk", "5")

//                        var photoURI = FileProvider.getUriForFile(this,
//                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
//                            photoFile!!
//                        )
                    val photoURI: Uri = FileProvider
                        .getUriForFile(this, this.getApplicationContext().getPackageName().toString() + ".provider", photoFile!!)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
//                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
            }
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create the File where the photo should go
            try {

                photoFile = createImageFile()
//                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
                Log.i("Mayank", photoFile!!.getAbsolutePath())

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("clickkk", "5")

//                        var photoURI = FileProvider.getUriForFile(this,
//                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
//                            photoFile!!
//                        )
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        this.getApplicationContext().getPackageName().toString() + ".provider",
                        photoFile!!
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
//                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
            }


        }


    }


    private fun createImageFile4(): File? {
        // External sdcard location
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                displayMessage(baseContext, "Unable to create directory.")
                return null
            }
        }

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        return File(
            mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg"
        )

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath

        return image
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                captureImage()
            }
        }

    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    fun obtieneLocalizacion(
        activity: Activity,
        fusedLocationClient: FusedLocationProviderClient,
        gpsInput: TextInputEditText?
    ) {
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
                    lat = location?.latitude.toString()
                    long = location?.longitude.toString()
                } else {
//                    message("Location error", activity)
//                    statusCheck(activity)
                }
            }
        }
    }


}
