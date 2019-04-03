package com.qiyi.video.reader.skin.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * author wander
 * date 2019/3/19
 *
 */
object SkinConfig {
    private const val skinPath = "skinPath"
    const val resTypeColor = "color"
    const val resTypeDrawable = "drawable"
    const val resTypeString = "string"

    /***
     * 支持的命名空间
     */
    const val SKIN_XML_NAMESPACE = "http://schemas.android.com/android/skin"

    /**界面元素支持换肤的属性 */
    const val ATTR_SKIN_ENABLE = "enable"
    const val SUPPORTED_ATTR_SKIN_LIST = "attrs"


    fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences("Skin_Pref", Context.MODE_PRIVATE)
    }


    fun getCurrentSkinPath(context: Context): String? {
        return getPref(context).getString(skinPath, null)
    }

    fun saveSkinPath(context: Context, path: String) {
        getPref(context).edit().putString(skinPath, path).apply()
    }

}