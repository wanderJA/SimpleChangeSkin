package com.qiyi.video.reader.skin

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.IntegerRes
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.skinDeployer.SkinDeployerFactory
import com.qiyi.video.reader.skin.utils.PluginLoadUtils
import com.qiyi.video.reader.skin.utils.SkinConfig
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
        val file = File(currentSkinPath)
        if (!file.exists()) {
            return
        }

        val pluginResources = PluginLoadUtils.getPluginResources(context, currentSkinPath)
        val packageInfo = PluginLoadUtils.getPackageInfo(context, currentSkinPath)
        if (packageInfo == null || pluginResources == null) {
            return
        }
        val skinPackageName = packageInfo.packageName

        if (TextUtils.isEmpty(skinPackageName)) {
            return
        }

        skinResourceManager.mSkinPluginResources = pluginResources
        skinResourceManager.mSkinPluginPackageName = skinPackageName

        SkinConfig.saveSkinPath(context, currentSkinPath)

        pluginSkinPath = currentSkinPath

        notifySkinChanged()
    }

    private fun notifySkinChanged() {

    }

    fun restoreSkin() {

    }

    fun setTextViewColor(view: TextView, @ColorRes resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.TEXT_COLOR, resId)
    }

    fun setImageResource(view: ImageView, @IntegerRes resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.IMAGE_RES, resId)
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
        SkinDeployerFactory.getSkinDeployer(parseSkinAttr.attrName)?.deploy(view, parseSkinAttr, skinResourceManager)
    }


}