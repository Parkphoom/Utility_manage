package com.example.utility_manage.Retrofit.Data

import com.example.utility_manage.Retrofit.Jsobj
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
    private var gps: Jsobj? = null

    @SerializedName("meterValues")
    @Expose
    private var waterMeter: String? = null

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

    fun getgps(): Jsobj? { return gps }
    fun setgps(gps: Jsobj?) { this.gps = gps }

    fun getwaterMeter(): String? { return waterMeter }
    fun setwaterMeter(waterMeter: String?) { this.waterMeter = waterMeter }

    fun getname(): String? { return name }
    fun setname(name: String?) { this.name = name }

    fun gettel(): String? { return tel }
    fun settel(tel: String?) { this.tel = tel }



    fun getbuildingtype(): String? { return buildingtype }
    fun setbuildingtype(buildingtype: String?) { this.buildingtype = buildingtype }

}