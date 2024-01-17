package com.usaminfotech.basicfunctions

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.Formatter
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import com.usaminfotech.basicfunctions.Constants.currentApiVersion
import com.usaminfotech.basicfunctions.Constants.datePickerDialog
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar

object Functions {
    fun getScreenWidth(context: Context, getScreenSize: Double): Double {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels / getScreenSize
    }

    fun getScreenHeight(context: Context, getScreenSize: Double): Double {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels / getScreenSize
    }
    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    fun getDeviceIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
    }
    fun getDeviceID(context: Context): String {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }
    fun internetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    private val calendar = Calendar.getInstance()
   fun getDateWithFormat(context: Context,dateView: TextView,format:String){
        val mYear = calendar[Calendar.YEAR]
        val mMonth = calendar[Calendar.MONTH]
        val mDay = calendar[Calendar.DAY_OF_MONTH]

        datePickerDialog = DatePickerDialog(context,
            { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar[year, monthOfYear] = dayOfMonth
                val format1 = SimpleDateFormat(format)
                dateView.text=format1.format(calendar.time).toString()
            }, mYear, mMonth, mDay
        )
        datePickerDialog!!.show()
    }

    fun imageConvertBase64(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)
        val image = BitmapFactory.decodeStream(input, null, null)
        // Encode image to base64 string
        val bos = ByteArrayOutputStream()
        image!!.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val imageBytes = bos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun openWhatsApp(context: Context,smsNumber:String) {
        val packageManager: PackageManager = context.packageManager
        val i = Intent(Intent.ACTION_VIEW)
        try {
            val url =
                "https://api.whatsapp.com/send?phone=$smsNumber&text=" + URLEncoder.encode(
                    "Hello",
                    "UTF-8"
                )
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i)
            }
            else{
                val url = "https://api.whatsapp.com/send?phone=$smsNumber"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)

            }
        } catch (e: Exception) {
            toast(context,"Please Install Whatsapp.")
            e.printStackTrace()
        }
    }
    fun hideSystemBars(window: Window) {
        currentApiVersion = Build.VERSION.SDK_INT
        val flags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = flags
            val decorView: View = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN === 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        }
    }

    fun convertImageToByte(context: Context, uri: Uri?): ByteArray? {
        var data: ByteArray? = null
        try {
            val cr = context.contentResolver
            val inputStream: InputStream? = cr.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            data = baos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return data
    }

}