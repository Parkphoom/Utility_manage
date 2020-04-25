package com.wac.utility_manage.Retrofit.Data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class regisMemberData {

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("meterId")
    @Expose
    var meterid: String? = null

    @SerializedName("gps")
    @Expose
    private var gps: GpsObj? = null

    @SerializedName("meterVal")
    @Expose
    private var meterVal: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("tel")
    @Expose
    var tel: String? = null



    @SerializedName("buildingType")
    @Expose
    var buildingtype: String? = null

    fun getaddress(): String? { return address }
    fun setaddress(address: String?) { this.address = address }

    fun getmeterid(): String? { return meterid }
    fun setmeterid(meterid: String?) { this.meterid = meterid }

    fun getgps(): GpsObj? { return gps }
    fun setgps(gps: GpsObj?) { this.gps = gps }

    fun getmeterVal(): String? { return meterVal }
    fun setmeterVal(meterVal: String?) { this.meterVal = meterVal }

    fun getname(): String? { return name }
    fun setname(name: String?) { this.name = name }

    fun gettel(): String? { return tel }
    fun settel(tel: String?) { this.tel = tel }



    fun getbuildingtype(): String? { return buildingtype }
    fun setbuildingtype(buildingtype: String?) { this.buildingtype = buildingtype }

}