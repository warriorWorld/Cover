package com.harbinger.cover

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.harbinger.cover.service.CoverService
import com.harbinger.cover.utils.ServiceUtil


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val mH: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "on resume")
        if (!isServiceStarted()) {
            startService()
        }
        finish()
    }

    private fun startService() {
        runOnUiThread {
            val intent = Intent(applicationContext, CoverService::class.java)
            startService(intent)
        }
    }

    private fun stopService() {
        runOnUiThread {
            val stopIntent = Intent(applicationContext, CoverService::class.java)
            stopService(stopIntent)
        }
    }

    private fun isServiceStarted(): Boolean {
        return ServiceUtil.isServiceWork(applicationContext, CoverService.NAME)
    }
}