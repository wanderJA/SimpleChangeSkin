package com.qiyi.video.reader.skin

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.IntegerRes
import android.support.annotation.MainThread
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.skinDeployer.SkinDeployerFactory
import com.qiyi.video.reader.skin.utils.PluginLoadUtils
import com.qiyi.video.reader.skin.utils.SkinConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * author wander
 * date 2019/3/19
 *
 */
@SuppressLint("StaticFieldLeak")
object SkinManager {
    val tag = javaClass.simpleName

    private lateinit var context: Context
    var pluginSkinPath: String? = null
    lateinit var skinResourceManager: SkinResourceManager
    private val mSkinViewMap = WeakHashMap<View, HashMap<String, SkinAttribute>>()

    @MainThread
    fun init(context: Context) {
        this.context = context.applicationContext
        skinResourceManager = SkinResourceManager(context.resources)
        loadCurrentSkin()
    }

    private fun loadCurrentSkin() {
        val currentSkinPath = SkinConfig.getCurrentSkinPath(context)
        if (currentSkinPath.isNullOrEmpty()) {
            restoreSkin()
        } else {
            loadNewSkin(currentSkinPath)
        }
    }

    fun loadNewSkin(currentSkinPath: String?) {
        if (currentSkinPath.isNullOrEmpty()) {
            return
        }

        GlobalScope.launch {
            val file = File(currentSkinPath)
            if (!file.exists()) {
                return@launch
            }
            Log.d(tag, "thread ${Thread.currentThread().name}")
            val pluginResources = PluginLoadUtils.getPluginResources(context, currentSkinPath)
            val packageInfo = PluginLoadUtils.getPackageInfo(context, currentSkinPath)
            if (packageInfo == null || pluginResources == null) {
                return@launch
            }
            val skinPackageName = packageInfo.packageName

            if (TextUtils.isEmpty(skinPackageName)) {
                return@launch
            }

            skinResourceManager.mSkinPluginResources = pluginResources
            skinResourceManager.mSkinPluginPackageName = skinPackageName

            SkinConfig.saveSkinPath(context, currentSkinPath)

            pluginSkinPath = currentSkinPath

            launch(Dispatchers.Main) {
                notifySkinChanged()
            }
        }

    }

    private fun notifySkinChanged() {
        mSkinViewMap.iterator().forEach {
            val view = it.key
            val hashMap = it.value
            if (view != null) {
                hashMap.iterator().forEach { attrMap ->
                    deploySkin(view, attrMap.value)
                }
            }
        }
    }

    fun restoreSkin() {
        skinResourceManager.restoreSkinDefault()
        notifySkinChanged()

    }


    private fun setSkinViewResource(view: View, attrName: String, resId: Int) {
        val parseSkinAttr = SkinAttribute.parseSkinAttr(view.context, attrName, resId)
        parseSkinAttr?.let {
            deploySkin(view, it)
            saveSkinView(view, it)
        }
    }

    private fun saveSkinView(view: View, attribute: SkinAttribute) {
        val skinView = mSkinViewMap[view]
        if (skinView != null) {
            skinView[attribute.attrName] = attribute
        } else {
            val hashMap = HashMap<String, SkinAttribute>()
            hashMap[attribute.attrName] = attribute
            mSkinViewMap[view] = hashMap
        }
    }

    private fun deploySkin(view: View, parseSkinAttr: SkinAttribute) {
        try {
            SkinDeployerFactory.getSkinDeployer(parseSkinAttr.attrName)
                ?.deploy(view, parseSkinAttr, skinResourceManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setTextViewColor(view: TextView, @ColorRes resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.TEXT_COLOR, resId)
    }

    fun setImageResource(view: ImageView, resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.IMAGE_RES, resId)
    }


    fun setBackground(view: View, resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.BACKGROUND, resId)

    }

}