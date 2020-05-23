package com.wac.utility_manage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utility_manage.R
import com.roacult.backdrop.BackdropLayout
import com.wac.utility_manage.PublicAction.Publicfunction
import com.wac.utility_manage.PublicAction.Publiclayout
import com.wac.utility_manage.Retrofit.Data.findMeterWaterData
import com.wac.utility_manage.Retrofit.retrofitCallback
import com.wac.utility_manage.Retrofit.retrofitCallfuntion
import com.wac.utility_manage.Search.SearchAdapter
import com.wac.utility_manage.Search.SearchItem
import me.samlss.broccoli.Broccoli
import me.samlss.broccoli.BroccoliGradientDrawable
import me.samlss.broccoli.PlaceholderParameter
import org.json.JSONArray
import org.json.JSONObject


class FindinvoiceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var pubF: Publicfunction
    private lateinit var retrofitCallfuntion: retrofitCallfuntion
    private var adapter: SearchAdapter? = null
    var recyclerView: RecyclerView? = null
    private val searchList: MutableList<SearchItem?> = ArrayList()
    private var backdropLayout: BackdropLayout? = null
    private var Cardcontainer: LinearLayout? = null
    private var menu: Menu? = null
    private var txtnovoice: TextView? = null
    private var broccoli: Broccoli? = Broccoli()

    private var txtline1: TextView? = null
    private var txtline2: TextView? = null
    private var txtline3: TextView? = null
    private var txtline4: TextView? = null
    private var txtline5: TextView? = null
    private var txtline6: TextView? = null
    private var txtline7: TextView? = null
    private var txtline8: TextView? = null
    private var txtline9: TextView? = null
    private var txtline10: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findinvoice)
        setUI()

    }

    override fun onStart() {
        super.onStart()
        RetrofitTask().execute()
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

    override fun onClick(v: View?) {
        val item_id = v?.id
        when (item_id) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu
        menuInflater.inflate(R.menu.closemenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id: Int = item.itemId
        return if (id == R.id.menu_close) {
            backdropLayout?.switch()

            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setUI() {
        pubF = Publicfunction()
        retrofitCallfuntion = retrofitCallfuntion()

        var actionBar: ActionBar? = null
        actionBar = supportActionBar
        Publiclayout()
            .setActionBar(this.resources.getString(R.string.findwateruser), actionBar)
        pubF.Slideleft(this)
        Cardcontainer = findViewById(R.id.card_container)
        txtnovoice = findViewById(R.id.txtnoinvoice)

        txtline1 = findViewById(R.id.searchtext_view1)
        setUpSkeleton(txtline1)
        txtline2 = findViewById(R.id.searchtext_view2)
        setUpSkeleton(txtline2)
        txtline3 = findViewById(R.id.searchtext_view3)
        setUpSkeleton(txtline3)
        txtline4 = findViewById(R.id.searchtext_view4)
        setUpSkeleton(txtline4)
        txtline5 = findViewById(R.id.searchtext_view5)
        setUpSkeleton(txtline5)
        txtline6 = findViewById(R.id.searchtext_view6)
        setUpSkeleton(txtline6)
        txtline7 = findViewById(R.id.searchtext_view7)
        setUpSkeleton(txtline7)
        txtline8 = findViewById(R.id.searchtext_view8)
        setUpSkeleton(txtline8)
        txtline9 = findViewById(R.id.searchtext_view9)
        setUpSkeleton(txtline9)
        txtline10 = findViewById(R.id.searchtext_view10)
        setUpSkeleton(txtline10)
        backdropLayout = findViewById(R.id.container)
        backdropLayout?.setOnBackdropChangeStateListener {
            when (it) {
                BackdropLayout.State.OPEN -> {
                    Log.d("drop", "1")
                    val item = menu!!.findItem(R.id.menu_close)
                    item.setIcon(R.drawable.icons8_sort_up_30px)
                }
                BackdropLayout.State.CLOSE -> {
                    Log.d("drop", "2")
                    val item = menu!!.findItem(R.id.menu_close)
                    item.setIcon(R.drawable.icons8_slider_30px_1)
                }
            }
        }


    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action === MotionEvent.ACTION_DOWN) {
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


    inner class RetrofitTask : AsyncTask<Void, Void, Int>() {
        override fun doInBackground(vararg arg0: Void?): Int? {
            retrofitCallfuntion.getAllinvoice(this@FindinvoiceActivity,
                object : retrofitCallback {
                    override fun onSucess(value: JSONObject) {

                    }

                    override fun onSucess(value: JSONArray) {

                        for (i in 0 until value.length()) {
                            val jsonmessage: JSONObject? = value.getJSONObject(i)
                            val _id = jsonmessage!!.getString("_id")
                            Log.d("res_CallAllinvoice", _id)
                            var ref1: String = ""
                            var category: String = ""
                            var startDate: String = ""
                            val finance = jsonmessage.getJSONArray("finance")

                            for (i in 0 until finance.length()) {
                                ref1 = finance.getJSONObject(i).getString("ref1")
                                    .replace("เลขที่บ้าน ", "")
                                category = finance.getJSONObject(i).getString("category")
                                startDate = finance.getJSONObject(i).getString("startDate")
                                Log.d("res_CallAllinvoice_ref1", ref1)

                            }
                            searchList.add(
                                SearchItem(
                                    "${finance.length()} รายการ", "บ้านเลขที่ $ref1", finance
                                )
                            )

                            adapter = SearchAdapter(
                                recyclerView,
                                this@FindinvoiceActivity,
                                searchList
                            )
                            txtnovoice!!.visibility = View.GONE
                            Cardcontainer!!.removeAllViews()
                            recyclerView!!.adapter = adapter
                        }
                    }

                    override fun onFailure() {}
                })

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            setUpRecyclerView()


        }
    }

    fun Callfinduser(findMeterData: findMeterWaterData) {
        retrofitCallfuntion.findMeterUser(this@FindinvoiceActivity, findMeterData,
            object : retrofitCallback {
                override fun onSucess(value: JSONObject) {
                    val data = value.getJSONObject("home").getJSONObject("data")
                    Log.d("res_Callfinduser", data.toString())
                    val user = value.getJSONObject("home").getJSONArray("user").getJSONObject(0)
                    Log.d("res_Callfinduser", user.toString())

//                    txthomdid?.setText(data.getString("address"))
//                    txtmeterid?.setText(data.getString("meterId"))
//                    txtbuildingtype?.setText(data.getString("buildingType"))
//                    startvaluesinput?.setText(data.getString("meterVal"))
//                    txtaddress?.setText(data.getString("address"))
//                    txtname?.setText(user.getString("name"))
//                    txttelnum?.setText(user.getString("tel"))
                }

                override fun onSucess(value: JSONArray) {
                }

                override fun onFailure() {}
            })
    }


    private fun setUpRecyclerView() {
        recyclerView = findViewById<View>(R.id.list_home) as RecyclerView

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = layoutManager

//        initScrollListener()
    }

    private fun setUpSkeleton(view: TextView?) {
        broccoli!!.addPlaceholder(
            PlaceholderParameter.Builder()
                .setView(view)
                .setDrawable(
                    BroccoliGradientDrawable(
                        Color.parseColor("#DDDDDD"),
                        Color.parseColor("#CCCCCC"), 40F, 800, LinearInterpolator()
                    )
                )
                .build()
        )


        broccoli!!.show()
    }

}
