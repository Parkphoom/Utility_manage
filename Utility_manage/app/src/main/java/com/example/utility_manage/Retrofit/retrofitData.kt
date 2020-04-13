package com.example.utility_manage.Retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class retrofitData {

    @SerializedName("ref1")
    @Expose
    var ref_1: String? = null

    @SerializedName("ref2")
    @Expose
    var ref_2: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null

    @SerializedName("startdate")
    @Expose
    private var startDate: String? = null

    @SerializedName("duedate")
    @Expose
    private var dueDate: String? = null

    @SerializedName("valuesid")
    @Expose
    private var valuesID: String? = null

    @SerializedName("meterid")
    @Expose
    private var meterID: String? = null

    @SerializedName("gps")
    private var gps: Jsobj? = null



    fun getstartDate(): String? {
        return startDate
    }

    fun setstartDate(startDate: String?) {
        this.startDate = startDate
    }

    fun getdueDate(): String? {
        return dueDate
    }

    fun setdueDate(dueDate: String?) {
        this.dueDate = dueDate
    }

    fun getvaluesID(): String? {
        return valuesID
    }

    fun setvaluesID(valuesID: String?) {
        this.valuesID = valuesID
    }

    fun getmeterID(): String? {
        return meterID
    }

    fun setmeterID(meterID: String?) {
        this.meterID = meterID
    }
    fun getgps(): Jsobj? {
        return gps
    }

    fun setgps(gps: Jsobj?) {
        this.gps = gps
    }



}