package com.usaminfotech.basicfunctions

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.util.Calendar
import java.util.concurrent.Executors

object Constants {
    var datePickerDialog: DatePickerDialog? = null
    var currentApiVersion: Int = 0
    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())
    var bitmapImg: Bitmap? = null

}