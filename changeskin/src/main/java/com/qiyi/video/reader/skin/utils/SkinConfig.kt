package com.qiyi.video.reader.skin.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * author wander
 * date 2019/3/19
 *
 */
object SkinConfig {
    const val skinPath = "skinPath"
    const val resTypeColor = "color"
    const val resTypeDrawable = "drawable"
    const val resTypeColorList = "colorList"


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