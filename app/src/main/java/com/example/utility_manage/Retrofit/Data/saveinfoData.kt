package com.example.utility_manage.Retrofit.Data

import com.example.utility_manage.Retrofit.Jsobj
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class saveinfoData {

    @SerializedName("topic")
    @Expose
    var topic: String? = null

    @SerializedName("detail")
    @Expose
    var detail: String? = null

    @SerializedName("gps")
    @Expose
    private var gps: Jsobj? = null


    fun gettopic(): String? { return topic }
    fun settopic(topic: String?) { this.topic = topic }

    fun getdetail(): String? { return detail }
    fun setdetail(detail: String?) { this.detail = detail }

    fun getgps(): Jsobj? { return gps }
    fun setgps(gps: Jsobj?) { this.gps = gps }
}