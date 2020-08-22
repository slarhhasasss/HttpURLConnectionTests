package ru.kolesnikovdmitry.httpurlconnectiontest.Activities

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_url.*
import ru.kolesnikovdmitry.httpurlconnectiontest.R

class AddUrlActivity: AppCompatActivity() {

    var mUrlForDownLoad = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_url)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        mUrlForDownLoad = intent?.getStringExtra(MainActivity.KEY_URL)!!

        editTextUrlActAddUrl.setText(mUrlForDownLoad)

        btnAddUrlActADdUrl.setOnClickListener {
            onClickBtnAddUrl()
        }
    }

    private fun onClickBtnAddUrl() {
        mUrlForDownLoad = editTextUrlActAddUrl.text.toString()
        if (mUrlForDownLoad == "") {
            Snackbar.make(btnAddUrlActADdUrl, getString(R.string.no_url), Snackbar.LENGTH_SHORT).show()
            return
        }
        intent.putExtra(MainActivity.KEY_URL, mUrlForDownLoad)
        setResult(Activity.RESULT_OK, intent)
        finish()
        return
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}