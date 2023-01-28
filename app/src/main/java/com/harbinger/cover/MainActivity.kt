package com.harbinger.cover

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 1111
    }

    private var cacheWidth = 0
    private var cacheHeight = 0
    private lateinit var sb_width: SeekBar
    private lateinit var sb_height: SeekBar
    private lateinit var bt_open_authority: Button
    private lateinit var bt_close: Button
    private val mH = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bt_open_authority = findViewById(R.id.bt_open_authority)
        sb_width = findViewById(R.id.sb_width)
        sb_height = findViewById(R.id.sb_height)
        bt_close = findViewById(R.id.bt_close)
        //打开权限
        bt_open_authority.setOnClickListener {
            startFloatingService()
        }
        sb_width.max = AndroidUtils.getScreeenWidth(this)
        sb_width.progress = FloatingService.DEFAULT_WIDTH

        sb_width.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, p2: Boolean) {
                updateWidth(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Caches.width = cacheWidth
            }
        })

        sb_height.max = AndroidUtils.getScreeenHeight(this)
        sb_height.progress = FloatingService.DEFAULT_HEIGHT
        sb_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, p2: Boolean) {
                updateHeight(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Caches.height = cacheHeight
            }
        })

        mH.postDelayed(Runnable {
            if (Caches.width > 0) {
                sb_width.progress = Caches.width
              updateWidth(Caches.width)
            }
            if (Caches.height > 0) {
                sb_height.progress = Caches.height
                updateHeight(Caches.height)
            }
        }, 500)

        bt_close.setOnClickListener {
            IntentSkip.startFloatService(this@MainActivity, startOrHide = false)
        }
    }

    private fun updateWidth(progress: Int) {
        IntentSkip.startFloatService(this@MainActivity, width = progress, height = -1)
        cacheWidth = progress
    }

    private fun updateHeight(progress: Int) {
        IntentSkip.startFloatService(this@MainActivity, width = -1, height = progress)
        cacheHeight = progress
    }

    //开启悬浮窗的service
    fun startFloatingService() {
        val hasAuthority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }

        if (!hasAuthority) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT)
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ), REQUEST_CODE
            )
        } else {
            //打开弹窗
            IntentSkip.startFloatService(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val hasAuthority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(this)
            } else {
                true
            }
            if (!hasAuthority) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                //打开弹窗
                IntentSkip.startFloatService(this)
            }
        }
    }
}
