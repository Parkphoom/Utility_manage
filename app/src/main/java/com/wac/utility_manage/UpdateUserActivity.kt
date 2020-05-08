package com.wac.utility_manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Address
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.utility_manage.R
import com.google.android.gms.location.LocationServices
import com.smartlib.addresspicker.AddressPickerActivity
import com.smartlib.addresspicker.AddressPickerActivity.Companion.RESULT_ADDRESS
import com.smartlib.addresspicker.MyLatLng
import com.smartlib.addresspicker.Pin
import com.wac.utility_manage.PublicAction.Printfuntion
import com.wac.utility_manage.PublicAction.PublicValues
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.addInvoiceData
import com.wac.utility_manage.Retrofit.retrofitCallfuntion


class UpdateUserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var prtF: Printfuntion
    private var dataInvoice = addInvoiceData()
    private lateinit var retrofitCallfuntion: retrofitCallfuntion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateuser)

        setUI()

        runOnUiThread {
            // Stuff that updates the UI
            val intent = Intent(this, AddressPickerActivity::class.java)
            intent.putExtra(AddressPickerActivity.ARG_LAT_LNG, MyLatLng(42.5328966, -122.7751082))
            val pinList=ArrayList<Pin>()
            pinList.add(Pin(MyLatLng(42.329989, -122.3100),"Work"))
            pinList.add(Pin(MyLatLng(42.023123, -122.23414),"Home"))
            intent.putExtra(AddressPickerActivity.ARG_LIST_PIN,  pinList)
            intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL,  1.0f)
            startActivityForResult(intent,PublicValues().ADDRESS_PICKER_REQUEST )
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PublicValues().ADDRESS_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val address: Address? = data?.getParcelableExtra(RESULT_ADDRESS) as Address
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

    override fun onStart() {
        super.onStart()
    }

    private fun setUI() {
        pubF = Publicfunction()
        prtF = Printfuntion()

        retrofitCallfuntion = retrofitCallfuntion()
        pubF.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var actionBar: ActionBar? = null
        actionBar = getSupportActionBar()
        Publiclayout().setActionBar(this.resources.getString(R.string.headerupdateuser), actionBar)
        pubF.Slideleft(this)


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








}
