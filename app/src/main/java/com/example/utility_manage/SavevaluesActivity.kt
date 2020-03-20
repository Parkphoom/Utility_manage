package com.example.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SavevaluesActivity : AppCompatActivity(), View.OnClickListener {
    private var pictureImagePath = ""
    private val Image_Capture_Code2 = 2
    private var cambtn: ImageButton? = null
    private var imgcam: ImageView? = null
    private var gpsInput: TextInputEditText? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private val IMAGE_DIRECTORY_NAME = "WAC"
    private lateinit var mCurrentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savevalues)

        setUI()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                obtieneLocalizacion()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Image_Capture_Code2) {
//            if (resultCode == Activity.RESULT_OK) {
//                Log.d("picturee", pictureImagePath)
////                imgcam!!.setImageBitmap(data!!.extras!!.get("data") as Bitmap?)
//                val file = File(pictureImagePath)
//                if (file.exists()) {
//                    Log.d("picturee_2222", file.name)
//                    val oldBitmap = BitmapFactory.decodeFile(file.absolutePath)
////                    val newBitmap: Bitmap =
////                        com.example.visitorpass.Fragment.selectvisit.selectvisitFragment.scaleDown(
////                            BitmapFactory.decodeFile(file.absolutePath),
////                            144f,
////                            true
////                        )
//                    val bos = ByteArrayOutputStream()
//                    oldBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
//                    imgcam!!.setImageDrawable(null)
//                    imgcam!!.setImageBitmap(oldBitmap)
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Publicfunction().message(getString(R.string.cancel), this)
//            }
//        }

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
            imgcam!!.setImageBitmap(myBitmap)
        } else {
//            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }


    private fun setUI() {
        cambtn = findViewById(R.id.opencambtn)
        cambtn!!.setOnClickListener(this)

        imgcam = findViewById(R.id.imgfromcam)
        imgcam!!.setImageDrawable(getResources().getDrawable(R.drawable.icons8_home_500px_1))

        gpsInput = findViewById(R.id.gps_text_input)
        gpsInput!!.setOnClickListener(this)
    }

    //Don't forget to ask for permissions for ACCESS_COARSE_LOCATION
//and ACCESS_FINE_LOCATION
    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 5)

        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    gpsInput!!.setText("${location?.latitude} : ${location?.longitude}")
                    Log.d("locationn", location?.longitude.toString())
                    Log.d("locationn", location?.latitude.toString())
                }
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
//                displayMessage(baseContext, photoFile!!.getAbsolutePath())
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
//            displayMessage(baseContext, "Camera is not available." + e.toString())
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

}
