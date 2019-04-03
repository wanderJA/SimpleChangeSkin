package com.wander.simplechangeskin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.qiyi.video.reader.skin.SkinManager
import com.squareup.leakcanary.LeakCanary
import com.wander.simplechangeskin.business.CustomDeployerUtil

/**
 * author wander
 * date 2019/3/19
 *
 */
class App : Application(), Application.ActivityLifecycleCallbacks {
    private var activityCount = 0
    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        activityCount--
        if (activityCount == 0) {
            appHide = true
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activityCount++
        appHide = false
    }

    companion object {
        lateinit var instance:Application
        var appHide = true
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        CustomDeployerUtil.registerCustomSkinDeployer()
        SkinManager.init(this)

        registerActivityLifecycleCallbacks(this)

    }
}