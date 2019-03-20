package com.wander.simplechangeskin

import android.app.Application
import com.qiyi.video.reader.skin.SkinManager
import com.wander.simplechangeskin.business.CustomDeployerUtil

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
        CustomDeployerUtil.registerCustomSkinDeployer()
        SkinManager.init(this)
    }
}