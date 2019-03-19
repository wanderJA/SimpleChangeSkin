package com.wander.simplechangeskin

import android.app.Application
import com.qiyi.video.reader.skin.SkinManager

/**
 * author wander
 * date 2019/3/19
 *
 */
class App: Application() {
    companion object {
        lateinit var instance:Application
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        SkinManager.init(this)
    }
}