package com.qiyi.video.reader.skin

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.util.Log
import com.qiyi.video.reader.skin.attribute.SkinAttribute

/**
 * author wander
 * date 2019/3/19
 *
 */
class SkinResourceManager(private var mDefaultResources: Resources) {
    fun getColorStateList(skinAttribute: SkinAttribute): ColorStateList {
        if (mSkinPluginResources == null) {
            return getColorStateListCompat(mDefaultResources, skinAttribute.resId)
        }

        val realResId = getRealResId(skinAttribute)
        var realColorStateList: ColorStateList? = null
        try {
            realColorStateList = mSkinPluginResources?.let {
                return@let getColorStateListCompat(it, realResId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (realColorStateList == null) {
            try {
                realColorStateList = getColorStateListCompat(mDefaultResources, skinAttribute.resId)
            } catch (e: Exception) {
            }
        }
        //不是colorStateList直接获取color
        Log.d(tag, "not color list")
        return realColorStateList ?: ColorStateList.valueOf(getColor(skinAttribute))
    }
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

    @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
    private fun getColorCompat(resources: Resources, resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(resId, null)
        } else {
            resources.getColor(resId)
        }
    }

    @Suppress("DEPRECATION")
    private fun getColorStateListCompat(resources: Resources, resId: Int): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColorStateList(resId, null)
        } else {
            resources.getColorStateList(resId)
        }
    }

    fun restoreSkinDefault() {
        mSkinPluginPackageName = null
        mSkinPluginResources = null
    }

    fun getString(skinAttribute: SkinAttribute): CharSequence {
        if (mSkinPluginResources == null) {
            return mDefaultResources.getString(skinAttribute.resId)
        }

        val realResId = getRealResId(skinAttribute)
        var realString: CharSequence? = null
        try {
            realString = mSkinPluginResources?.let {
                return@let it.getString(realResId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return realString ?: mDefaultResources.getString(skinAttribute.resId)
    }

    fun getDimen(skinAttribute: SkinAttribute): Float {
        if (mSkinPluginResources == null) {
            return mDefaultResources.getDimension(skinAttribute.resId)
        }

        val realResId = getRealResId(skinAttribute)
        var realDimen: Float? = null
        try {
            realDimen = mSkinPluginResources?.let {
                return@let it.getDimension(realResId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return realDimen ?: mDefaultResources.getDimension(skinAttribute.resId)
    }


    private val tag = javaClass.simpleName
    var mSkinPluginPackageName: String? = null
    var mSkinPluginResources: Resources? = null

}