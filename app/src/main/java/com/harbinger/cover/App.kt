package com.harbinger.cover

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2023/1/28.
 */
class App :Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}