package ru.kolesnikovdmitry.httpurlconnectiontest.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kolesnikovdmitry.httpurlconnectiontest.R

class StartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        btnCheckStatusOfConnectionActStart.setOnClickListener {
            onClickCheckConnection()
        }

        btnDownloadPageActStart.setOnClickListener {
            onClickDownloadPage()
        }
    }

    private fun onClickDownloadPage() {
        if (isConnectedToInternet()) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        else {
            Snackbar.make(btnDownloadPageActStart, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onClickCheckConnection() {
        textViewStatusOfConnectionActStart.visibility = TextView.INVISIBLE
        progressBarActStart.visibility = ProgressBar.VISIBLE
        GlobalScope.launch {
            if (isConnectedToInternet()) {
                withContext(Dispatchers.Main) {
                    textViewStatusOfConnectionActStart.visibility = TextView.VISIBLE
                    progressBarActStart.visibility = ProgressBar.INVISIBLE
                    textViewStatusOfConnectionActStart.text = getString(R.string.connected)
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    textViewStatusOfConnectionActStart.visibility = TextView.VISIBLE
                    progressBarActStart.visibility = ProgressBar.INVISIBLE
                    textViewStatusOfConnectionActStart.text = getString(R.string.no_connection)
                }
            }
        }
    }


    //проверка подключения к интернету
    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            return true
        }
        return false
    }
}