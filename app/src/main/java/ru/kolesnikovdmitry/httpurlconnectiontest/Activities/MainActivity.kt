package ru.kolesnikovdmitry.httpurlconnectiontest.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kolesnikovdmitry.httpurlconnectiontest.R
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity: AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_ACT_URL = 101
        const val KEY_URL = "url"
    }

    var mUrlForDownload = ""

    private val ID_MENU_ITEM_ALEXANDER_KLIMOV = 102
    private val ID_GROUP_MENU_ITEMS = 101
    private val ID_MENU_ITEM_ALEX_KLIMOV_IMAGE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnDownTextActMain.setOnClickListener {
            onClickBtnDownloadText()
        }

        cardViewUrlActMain.setOnClickListener {
            onClickCardViewUrl()
        }

        btnDownImageActMain.setOnClickListener {
            onClickBtnDownloadImage()
        }
    }

    private fun onClickBtnDownloadImage() {
        if (mUrlForDownload.isEmpty()) {
            Snackbar.make(btnDownTextActMain, getString(R.string.no_url), Snackbar.LENGTH_SHORT).show()
            return
        }
        if (!isNetworkAvailable()) {
            Snackbar.make(btnDownTextActMain, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
            return
        }

        btnDownImageActMain.visibility = Button.INVISIBLE
        progressBarDownImageActMain.visibility = ProgressBar.VISIBLE

        GlobalScope.launch {
            val responseCode = checkResponseCode(mUrlForDownload)
            if (responseCode != HttpURLConnection.HTTP_OK.toString()) {
                val errorStr = "Error with code: $responseCode"
                withContext(Dispatchers.Main) {
                    btnDownImageActMain.visibility = Button.VISIBLE
                    progressBarDownImageActMain.visibility = ProgressBar.INVISIBLE
                    textViewInfoFromPageActMain.text = errorStr
                }
                return@launch
            }
            else {
                val imageFromServer : Bitmap? = downloadImageFromServer(mUrlForDownload)
                if (imageFromServer == null) {
                    withContext(Dispatchers.Main) {
                        textViewInfoFromPageActMain.text = getString(R.string.unreal_to_download)
                        btnDownImageActMain.visibility = Button.VISIBLE
                        progressBarDownImageActMain.visibility = ProgressBar.INVISIBLE
                    }
                }
                else {
                    withContext(Dispatchers.Main) {
                        imageViewActMain.setImageBitmap(imageFromServer)
                        btnDownImageActMain.visibility = Button.VISIBLE
                        progressBarDownImageActMain.visibility = ProgressBar.INVISIBLE
                    }
                }
            }
        }
    }

    private fun downloadImageFromServer(strUrl: String): Bitmap? {
        return try {
            val url = URL(strUrl)
            val connection = url.openConnection() as HttpURLConnection

            //читываем данные
            val inputStream = connection.inputStream

            BitmapFactory.decodeStream(inputStream)
        } catch (th : Throwable) {
            null
        }
    }

    private fun onClickCardViewUrl() {
        val intent = Intent(applicationContext, AddUrlActivity::class.java)
        intent.putExtra(KEY_URL, mUrlForDownload)
        startActivityForResult(intent, REQUEST_CODE_ACT_URL)
    }

    private fun onClickBtnDownloadText() {
        if (mUrlForDownload.isEmpty()) {
            Snackbar.make(btnDownTextActMain, getString(R.string.no_url), Snackbar.LENGTH_SHORT).show()
            return
        }
        if (!isNetworkAvailable()) {
            Snackbar.make(btnDownTextActMain, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
            return
        }

        btnDownTextActMain.visibility = Button.INVISIBLE
        progressBarDownTextActMain.visibility = ProgressBar.VISIBLE

        GlobalScope.launch {
            val responseCode = checkResponseCode(mUrlForDownload)
            if (responseCode != HttpURLConnection.HTTP_OK.toString()) {
                withContext(Dispatchers.Main) {
                    btnDownTextActMain.visibility = Button.VISIBLE
                    progressBarDownTextActMain.visibility = ProgressBar.INVISIBLE
                    val strErrorInfo = "Error with code: $responseCode"
                    textViewInfoFromPageActMain.text = strErrorInfo
                    return@withContext
                }
                return@launch
            }
            else {
                val strFromServer : String = getStrFromServer(mUrlForDownload)
                withContext(Dispatchers.Main) {
                    textViewInfoFromPageActMain.text = strFromServer
                    btnDownTextActMain.visibility = Button.VISIBLE
                    progressBarDownTextActMain.visibility = ProgressBar.INVISIBLE
                }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        var available = false
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) available = true

        return available
    }

    private fun getStrFromServer(strUrl: String): String {
        try {
            val url = URL(strUrl)
            val connection = url.openConnection() as HttpURLConnection

            val inputStream = connection.inputStream
            val byteArrayOutputStream = ByteArrayOutputStream()

            var read = inputStream.read()
            while ((read) != -1) {
                byteArrayOutputStream.write(read)
                read = inputStream.read()
            }
            val result = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()
            return String(result)
        } catch (th : Throwable) {
            return "Error: ${th.message.toString()}"
        }
    }

    private fun checkResponseCode(strUrl: String) : String{
        return try {
            val url = URL(strUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 5000
            connection.connectTimeout = 5000

            connection.responseCode.toString()
        } catch (th: Throwable) {
            "(from catch)" + th.message.toString()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ACT_URL -> {
                infoFromAddUrlAct(resultCode, data)
            }
        }
    }

    private fun infoFromAddUrlAct(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                Toast.makeText(applicationContext, getString(R.string.not_saved), Toast.LENGTH_SHORT).show()
            }
            Activity.RESULT_OK -> {
                Toast.makeText(applicationContext, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                mUrlForDownload = data?.getStringExtra(KEY_URL)!!
                val strForTextView = "${getString(R.string.url)} $mUrlForDownload"
                textViewUrlActMain.text = strForTextView
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(ID_GROUP_MENU_ITEMS, ID_MENU_ITEM_ALEXANDER_KLIMOV, 1, getString(R.string.menu_item_alexander_klimov))
        menu.add(ID_GROUP_MENU_ITEMS, ID_MENU_ITEM_ALEX_KLIMOV_IMAGE, 2, getString(R.string.menu_item_alexander_klimov_image))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ID_MENU_ITEM_ALEXANDER_KLIMOV -> {
                setUpUrlFromMenu(ID_MENU_ITEM_ALEXANDER_KLIMOV)
            }
            ID_MENU_ITEM_ALEX_KLIMOV_IMAGE -> {
                setUpUrlFromMenu(ID_MENU_ITEM_ALEX_KLIMOV_IMAGE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpUrlFromMenu(menuItemId: Int) {
        when(menuItemId) {
            ID_MENU_ITEM_ALEXANDER_KLIMOV -> {
                mUrlForDownload = getString(R.string.url_alexander_klimov)
                val strForTextView = "${getString(R.string.url)} $mUrlForDownload"
                textViewUrlActMain.text = strForTextView
            }
            ID_MENU_ITEM_ALEX_KLIMOV_IMAGE -> {
                mUrlForDownload = getString(R.string.url_alexander_klimov_image)
                val strForTextView = "${getString(R.string.url)} $mUrlForDownload"
                textViewUrlActMain.text = strForTextView
            }
        }
    }


    /*
    Чтобы работал протокол Http надо в манифесте в теге application прописать android:usesCleartextTraffic="true"
     */
}