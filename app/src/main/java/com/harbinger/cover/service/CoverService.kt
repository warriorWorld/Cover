package com.harbinger.cover.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import com.harbinger.cover.R
import com.harbinger.cover.view.CoverView
import com.harbinger.cover.view.CoverView.OnCoverViewClickListener


/**
 * Created by acorn on 2023/1/28.
 */
public class CoverService : Service() {
    companion object {
        val NAME = "com.harbinger.cover.service.CoverService"
        private val TAG = "CoverService"
    }

    private val mH: Handler = Handler(Looper.getMainLooper())

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        mH.removeCallbacksAndMessages(null)
    }

    private fun initUI() {
        val layout: View = LayoutInflater.from(this).inflate(R.layout.cover_view, null)
        val coverView = CoverView(this, layout, object : OnCoverViewClickListener {
            override fun onCoverClick() {
            }

            override fun onCoverLongClick() {
            }
        })
    }
}