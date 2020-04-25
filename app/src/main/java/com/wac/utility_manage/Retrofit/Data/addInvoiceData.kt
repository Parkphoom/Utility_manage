package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class addInvoiceData {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: messageObj? = null

    class messageObj {
        @SerializedName("operate")
        @Expose
        var operate: String? = null

        @SerializedName("data")
        @Expose
        var data: String? = null

        @SerializedName("generation")
        @Expose
        var generation: String? = null
    }


    @SerializedName("ref1")
    @Expose
    var ref_1: String? = null

    @SerializedName("ref2")
    @Expose
    var ref_2: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null


    @SerializedName("startDate")
    @Expose
    private var startDate: String? = null

    @SerializedName("dueDate")
    @Expose
    private var dueDate: String? = null

    @SerializedName("meterVal")
    @Expose
    private var meterVal: String? = null

    @SerializedName("meterId")
    @Expose
    private var meterID: String? = null

    @SerializedName("gps")
    @Expose
    private var gps: GpsObj? = null

    @SerializedName("payment")
    @Expose
    private var payment: paymentObj? = null

    inner class paymentObj {
        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("amount")
        @Expose
        var amount: String? = null

        @SerializedName("credit")
        @Expose
        var credit: String? = null

        @SerializedName("remain")
        @Expose
        var remain: String? = null

        @SerializedName("via")
        @Expose
        var via: String? = null

        @SerializedName("creditDate")
        @Expose
        var creditdate: String? = null

        @SerializedName("receiveName")
        @Expose
        var receiveName: String? = null


    }


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

    fun getmeterVal(): String? {
        return meterVal
    }

    fun setmeterVal(meterVal: String?) {
        this.meterVal = meterVal
    }

    fun getmeterID(): String? {
        return meterID
    }

    fun setmeterID(meterID: String?) {
        this.meterID = meterID
    }

    fun getgps(): GpsObj? {
        return gps
    }

    fun setgps(gps: GpsObj?) {
        this.gps = gps
    }

    fun setpayment(payment: paymentObj?) {
        this.payment = payment
    }

    fun getpayment(): paymentObj? {
        return payment
    }




}