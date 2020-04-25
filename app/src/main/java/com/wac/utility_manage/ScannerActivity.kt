package com.wac.utility_manage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.utility_manage.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.shashank.sony.fancytoastlib.FancyToast
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import java.io.IOException

class ScannerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction

    var surfaceView: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    var intentData = ""
    private var inputRef2 :Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        initViews()
    }


    private fun initViews() {
        pubF = Publicfunction()

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(
            this.resources.getString(R.string.headerscan),
            actionBar
        )

        surfaceView = findViewById(R.id.surfaceView)
        inputRef2 = findViewById(R.id.inputRef2btn)
        inputRef2?.setOnClickListener ( this )
    }


    private fun initialiseDetectorsAndSources() {
        pubF.message("Barcode scanner started", FancyToast.INFO, this)

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScannerActivity,
                            Manifest.permission.CAMERA
                        ) === PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(surfaceView!!.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ScannerActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object :
            Detector.Processor<Barcode> {
            override fun release() {
//                pubF.message(
//                    "To prevent memory leaks barcode scanner has been stopped",
//                    FancyToast.WARNING,
//                    this@ScannerActivity
//                )

            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    intentData = barcodes.valueAt(0).displayValue

                    val intent = Intent(this@ScannerActivity, PaymentWaterActivity::class.java)
                    intent.putExtra("Ref2", intentData)
                    startActivity(intent)
                    finish()
//                        if (barcodes.valueAt(0).email != null) {
//                            txtBarcodeValue!!.removeCallbacks(null)
//                            intentData = barcodes.valueAt(0).email.address
//                            txtBarcodeValue!!.text = intentData
//                            isEmail = true
//                        } else {
//                            isEmail = false
//                            intentData = barcodes.valueAt(0).displayValue
//                            txtBarcodeValue!!.text = intentData
//                        }
                }
            }
        })


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
    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.inputRef2btn -> {
                val intent = Intent(this@ScannerActivity, PaymentWaterActivity::class.java)
                intent.putExtra("Ref2", "")
                startActivity(intent)
                finish()
            }
        }
    }


}
