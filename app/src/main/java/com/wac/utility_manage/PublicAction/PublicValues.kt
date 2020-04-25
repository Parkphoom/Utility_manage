package com.wac.utility_manage.PublicAction

import android.os.Environment
import java.io.File

class PublicValues {
    val NOPAPER = 3
    val LOWBATTERY = 4
    val PRINTVERSION = 5
    val PRINTBARCODE = 6
    val PRINTQRCODE = 7
    val PRINTPAPERWALK = 8
    val PRINTCONTENT = 9
    val CANCELPROMPT = 10
    val PRINTERR = 11
    val OVERHEAT = 12
    val MAKER = 13
    val PRINTPICTURE = 14
    val NOBLACKBLOCK = 15
    var wordFont:Int = 0

    val GPS_LOCATION_REQUEST = 100
     val ADDRESS_PICKER_REQUEST = 1020

     val root = Environment.getExternalStorageDirectory()
     val sd = File(root.absolutePath + "/WAC")

    val headercsvAddinvoice =
        """ref1,ref2,category,amount,startDate,dueDate,meterVal,meterId,latitude,longitude,payment

            """.trimIndent()
    val headercsvRegister =
        """address,buildingType,latitude,longitude,meterId,name,tel,materVal,status,password

            """.trimIndent()

    val CSVnameAddinvoice = "csv_file_addinvoice.csv"
    val CSVnameRegister = "csv_file_register.csv"

}