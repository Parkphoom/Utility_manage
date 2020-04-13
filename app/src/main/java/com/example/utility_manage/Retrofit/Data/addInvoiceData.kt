package com.example.utility_manage.Retrofit.Data

import com.example.utility_manage.Retrofit.Jsobj
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class addInvoiceData {

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

    @SerializedName("startDate")
    @Expose
    private var startDate: String? = null

    @SerializedName("dueDate")
    @Expose
    private var dueDate: String? = null

    @SerializedName("waterMeter")
    @Expose
    private var waterMeter: String? = null

    @SerializedName("meterId")
    @Expose
    private var meterID: String? = null

    @SerializedName("gps")
    @Expose
    private var gps: Jsobj? = null

    @SerializedName("payment")
    @Expose
    private var payment: String? = null


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

    fun getwaterMeter(): String? {
        return waterMeter
    }

    fun setwaterMeter(waterMeter: String?) {
        this.waterMeter = waterMeter
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

    fun setpayment(payment: String?) {
        this.payment = payment
    }

    fun getpayment(): String? {
        return payment
    }


}