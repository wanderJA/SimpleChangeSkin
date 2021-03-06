package com.qiyi.video.reader.skin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorRes
import android.support.annotation.MainThread
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.skinDeployer.SkinDeployerFactory
import com.qiyi.video.reader.skin.utils.ISkinChangeObserver
import com.qiyi.video.reader.skin.utils.OnLoadSkinListener
import com.qiyi.video.reader.skin.utils.PluginLoadUtils
import com.qiyi.video.reader.skin.utils.SkinConfig
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
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
    val mSkinViewMap = WeakHashMap<View, HashMap<String, SkinAttribute>>()
    private val skinChangeObservers = ArrayList<ISkinChangeObserver>()

    @MainThread
    fun init(context: Context) {
        this.context = context.applicationContext
        skinResourceManager = SkinResourceManager(context.resources)
        loadCurrentSkin()
    }


    fun destroy() {
        mSkinViewMap.clear()
        skinChangeObservers.clear()
    }

    /**
     * 在view需要被释放时，主动调用该方法，可以立即从缓存中释放，降低内存泄漏
     */
    fun releaseViewImmediately(context: Context) {
        val iterator = mSkinViewMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val view = entry.key
            if (view != null && view.context === context) {
                iterator.remove()
            }
        }
    }

    private fun loadCurrentSkin() {
        val currentSkinPath = SkinConfig.getCurrentSkinPath(context)
        if (currentSkinPath.isNullOrEmpty()) {
            restoreSkin()
        } else {
            loadNewSkin(currentSkinPath)
        }
    }

    fun loadNewSkin(newSkinPath: String?, onLoadSkinListener: OnLoadSkinListener? = null) {
        if (newSkinPath.isNullOrEmpty()) {
            return
        }
        object : AsyncTask<String, Unit, Unit>() {
            override fun doInBackground(vararg params: String?) {
                if (!File(newSkinPath).exists()) {
                    onLoadSkinListener?.onFail()
                    return
                }
                val pluginResources = PluginLoadUtils.getPluginResources(context, newSkinPath)
                val packageInfo = PluginLoadUtils.getPackageInfo(context, newSkinPath)
                if (packageInfo == null || pluginResources == null) {
                    onLoadSkinListener?.onFail()
                    return
                }
                val skinPackageName = packageInfo.packageName

                if (TextUtils.isEmpty(skinPackageName)) {
                    onLoadSkinListener?.onFail()
                    return
                }

                skinResourceManager.mSkinPluginResources = pluginResources
                skinResourceManager.mSkinPluginPackageName = skinPackageName

                SkinConfig.saveSkinPath(context, newSkinPath)

                pluginSkinPath = newSkinPath
            }


            override fun onPostExecute(result: Unit?) {
                Handler(Looper.getMainLooper()).post {
                    notifySkinChanged()
                    onLoadSkinListener?.onSuccess()
                }
            }

        }.execute(newSkinPath)
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
        skinChangeObservers.forEach {
            it.onChanged(pluginSkinPath)
        }

    }

    fun restoreSkin() {
        skinResourceManager.restoreSkinDefault()
        pluginSkinPath = null
        SkinConfig.saveSkinPath(context, "")
        notifySkinChanged()

    }

    /**
     * 设置需要换肤的view
     * 获取到皮肤中对应的资源，然后应用到view中
     * @param attrName view 需要设置的属性，保持唯一，对应[SkinDeployerFactory.supportSkinDeployer]
     */
    fun setSkinViewResource(view: View, attrName: String, resId: Int): Boolean {
        val parseSkinAttr = SkinAttribute.parseSkinAttr(view.context, attrName, resId)
        parseSkinAttr?.let {
            deploySkin(view, it)
            saveSkinView(view, it)
        }
        return parseSkinAttr != null
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

    /**
     * 设置字体的color
     */
    fun setTextViewColorStateList(view: TextView, @ColorRes resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.COLOR_LIST, resId)
    }

    fun setImageResource(view: ImageView, resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.SRC, resId)
    }

    /**
     * 设置view背景
     * @param resId color  drawable
     */
    fun setBackground(view: View, resId: Int) {
        setSkinViewResource(view, SkinDeployerFactory.BACKGROUND, resId)
    }

    fun setText(view: TextView?, resId: Int) {
        view?.let {
            setSkinViewResource(it, SkinDeployerFactory.TEXT_STRING, resId)
        }
    }

    fun addObserver(iSkinChangeObserver: ISkinChangeObserver) {
        skinChangeObservers.add(iSkinChangeObserver)
    }

    fun removeObserver(iSkinChangeObserver: ISkinChangeObserver) {
        skinChangeObservers.remove(iSkinChangeObserver)
    }

    //<editor-fold desc="仅获取资源，业务层直接处理使用，需添加换肤监听">
    /**
     * @param iSkinChangeObserver 需要移除
     */
    fun getSkinDrawable(resId: Int, iSkinChangeObserver: ISkinChangeObserver? = null): Drawable? {
        val skinAttribute = SkinAttribute.parseSkinAttr(context, SkinConfig.resTypeDrawable, resId)
        var drawable: Drawable? = null
        skinAttribute?.let {
            drawable = skinResourceManager.getDrawable(it)
        }
        if (iSkinChangeObserver != null) {
            addObserver(iSkinChangeObserver)
        }
        return drawable
    }

    fun getSkinColor(resId: Int, iSkinChangeObserver: ISkinChangeObserver? = null): Int {
        val skinAttribute = SkinAttribute.parseSkinAttr(context, SkinConfig.resTypeDrawable, resId)
        var color = 0
        skinAttribute?.let {
            color = skinResourceManager.getColor(it)
        }
        if (iSkinChangeObserver != null) {
            addObserver(iSkinChangeObserver)
        }
        return color
    }

    fun getSkinString(resId: Int, receiver: (CharSequence) -> Unit): CharSequence? {
        val skinAttribute = SkinAttribute.parseSkinAttr(context, SkinConfig.resTypeString, resId)
        var string: CharSequence? = null
        skinAttribute?.let {
            string = skinResourceManager.getString(it)
        }
        string?.let { receiver(it) }
        return string
    }
    //</editor-fold>

}