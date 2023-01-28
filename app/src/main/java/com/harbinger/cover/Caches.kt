package com.harbinger.cover

import com.tencent.mmkv.MMKV

/**
 * Created by acorn on 2023/1/1.
 */
object Caches {
    private val mmkv = MMKV.mmkvWithID("CoverCache", MMKV.MULTI_PROCESS_MODE)

    var width by mmkv.int()
    var height by mmkv.int()
}