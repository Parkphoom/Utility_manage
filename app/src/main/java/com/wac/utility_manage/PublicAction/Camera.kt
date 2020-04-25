package com.wac.utility_manage.PublicAction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Camera {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        captureImage()
//    } else {
//        captureImage2()
//
//    }



//    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
//    private fun captureImage2() {
//
//        try {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            photoFile = createImageFile4()
//            if (photoFile != null) {
//                pubF.message(photoFile!!.getAbsolutePath(), this)
//                Log.i("Mayank", photoFile!!.getAbsolutePath())
////                val photoURI = Uri.fromFile(photoFile)
//                val photoURI: Uri = FileProvider.getUriForFile(
//                    this,
//                    this.applicationContext.packageName.toString() + ".provider",
//                    photoFile!!
//                )
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
//            }
//        } catch (e: Exception) {
//            pubF.message("Camera is not available.$e", this)
//        }
//
//    }
//
//    private fun captureImage() {
//
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                0
//            )
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            // Create the File where the photo should go
//            try {
//
//                photoFile = createImageFile()
////                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
//                Log.i("Mayank", photoFile!!.getAbsolutePath())
//
//                // Continue only if the File was successfully created
//                if (photoFile != null) {
//                    Log.d("clickkk", "5")
//
////                        var photoURI = FileProvider.getUriForFile(this,
////                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
////                            photoFile!!
////                        )
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        this.getApplicationContext().getPackageName().toString() + ".provider",
//                        photoFile!!
//                    )
//
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
//
//                }
//            } catch (ex: Exception) {
//                // Error occurred while creating the File
////                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
//            }
//        } else {
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            // Create the File where the photo should go
//            try {
//
//                photoFile = createImageFile()
////                    displayMessage(baseContext, photoFile!!.getAbsolutePath())
//                Log.i("Mayank", photoFile!!.getAbsolutePath())
//
//                // Continue only if the File was successfully created
//                if (photoFile != null) {
//                    Log.d("clickkk", "5")
//
////                        var photoURI = FileProvider.getUriForFile(this,
////                            "com.vlemonn.blog.kotlincaptureimage.fileprovider",
////                            photoFile!!
////                        )
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        this.getApplicationContext().getPackageName().toString() + ".provider",
//                        photoFile!!
//                    )
//
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
//
//                }
//            } catch (ex: Exception) {
//                // Error occurred while creating the File
////                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
//            }
//
//
//        }
//
//
//    }
//
//    private fun createImageFile4(): File? {
//        // External sdcard location
//        val mediaStorageDir = File(
//            Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//            IMAGE_DIRECTORY_NAME
//        )
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
////                displayMessage(baseContext, "Unable to create directory.")
//                return null
//            }
//        }
//
//        val timeStamp = SimpleDateFormat(
//            "yyyyMMdd_HHmmss",
//            Locale.getDefault()
//        ).format(Date())
//
//        return File(
//            mediaStorageDir.path + File.separator
//                    + "IMG_" + timeStamp + ".jpg"
//        )
//
//    }
//
//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//            imageFileName, /* prefix */
//            ".jpg", /* suffix */
//            storageDir      /* directory */
//        )
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.absolutePath
//        return image
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == 0) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                && grantResults[1] == PackageManager.PERMISSION_GRANTED
//            ) {
//                captureImage()
//            }
//        }
//
//    }
}