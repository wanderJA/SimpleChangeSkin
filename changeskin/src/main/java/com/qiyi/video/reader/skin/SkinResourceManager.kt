package com.qiyi.video.reader.skin

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.qiyi.video.reader.skin.attribute.SkinAttribute

/**
 * author wander
 * date 2019/3/19
 *
 */
class SkinResourceManager(private var mDefaultResources: Resources) {
    @ColorInt
    fun getColor(skinAttribute: SkinAttribute): Int {
        if (mSkinPluginResources == null) {
            return getColorCompat(mDefaultResources, skinAttribute.resId)
        }

        val realResId = getRealResId(skinAttribute)
        var realColor = -1
        try {
            mSkinPluginResources?.let { realColor = getColorCompat(it, realResId) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (realColor == -1) {
            realColor = getColorCompat(mDefaultResources, skinAttribute.resId)
        }
        return realColor
    }

    fun getDrawable(skinAttribute: SkinAttribute): Drawable {
        if (mSkinPluginResources == null) {
            return getDrawableCompat(mDefaultResources, skinAttribute.resId)
        }

        val realResId = getRealResId(skinAttribute)
        var realDrawable: Drawable? = null
        try {
            realDrawable = mSkinPluginResources?.let {
                return@let getDrawableCompat(it, realResId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (realDrawable == null) {
            realDrawable = getDrawableCompat(mDefaultResources, skinAttribute.resId)
        }
        return realDrawable
    }

    private fun getDrawableCompat(resources: Resources, resId: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(resId, null)
        } else {
            resources.getDrawable(resId)
        }
    }

    private fun getRealResId(skinAttribute: SkinAttribute): Int {
        return mSkinPluginResources?.getIdentifier(
            skinAttribute.attrValueRefName,
            skinAttribute.attrValueTypeName,
            mSkinPluginPackageName
        ) ?: -1
    }

    private fun getColorCompat(resources: Resources, resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(resId, null)
        } else {
            resources.getColor(resId)
        }
    }


    private val tag = javaClass.simpleName
    var mSkinPluginPackageName: String? = null
    var mSkinPluginResources: Resources? = null

}