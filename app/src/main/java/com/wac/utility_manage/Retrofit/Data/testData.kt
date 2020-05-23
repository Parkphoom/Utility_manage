package com.wac.utility_manage.Retrofit.Data

import android.util.Log

class testData {
    private val id: String? = null

    private val dynamicValues: MutableMap<String, String> = HashMap()
    fun addDynamicValues(key: String, value: String) {
        Log.d("ressss_S", key)
        Log.d("ressss_S", value)
        dynamicValues.put(key, value)
    }
}