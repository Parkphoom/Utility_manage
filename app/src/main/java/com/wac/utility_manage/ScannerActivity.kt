package com.wac.utility_manage

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.utility_manage.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.textfield.TextInputEditText
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

    private var ref2 = ""
    @SuppressLint("InflateParams")
    private fun createref2input() {
        val builder = AlertDialog.Builder(
            this,
            R.style.CustomDialog
        )
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_ref2, null)

        builder.setView(view)
        builder.setCancelable(false)
        val alert = builder.create()

        val ref2input = view.findViewById<TextInputEditText>(R.id.ref2_text_input)
        val submitbtn = view.findViewById<Button>(R.id.submitbtn)
        submitbtn.setOnClickListener {
            if (!ref2input.text?.isEmpty()!!) {
                ref2 = ref2input.text.toString()

                val intent = Intent(this@ScannerActivity, PaymentWaterActivity::class.java)
                intent.putExtra("Ref2", ref2)
                startActivity(intent)
                finish()
            } else {
                pubF.message(getString(R.string.Inputref_2), FancyToast.ERROR, this)
            }

        }
        val closebtn = view.findViewById<ImageButton>(R.id.dialog_closebtn)
        closebtn.setOnClickListener {
            this.onBackPressed()
        }

        alert.show()
        val window = alert.window
        if (window != null) {

            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT

            window.attributes = wlp

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
                createref2input()

            }
        }
    }


}
