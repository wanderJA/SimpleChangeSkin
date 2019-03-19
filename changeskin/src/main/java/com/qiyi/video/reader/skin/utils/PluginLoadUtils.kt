package com.qiyi.video.reader.skin.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log

object PluginLoadUtils {

    fun getPackageInfo(context: Context, apkPath: String): PackageInfo? {
        return createPackageInfo(context, apkPath)
    }

    fun getPluginResources(context: Context, apkPath: String): Resources? {
        return createResources(context, apkPath)
    }
    /**
     * 创建AssetManager对象
     */
    private fun createAssetManager(dexPath: String): AssetManager? {
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, dexPath)
            return assetManager
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 创建Resource对象
     */
    private fun createResources(context: Context, assetManager: AssetManager?): Resources? {
        if (assetManager == null) {
            Log.e("createResources", " create Resources failed assetManager is NULL !! ")
            return null
        }
        val superRes = context.resources
        return Resources(assetManager, superRes.displayMetrics, superRes.configuration)
    }

    private fun createResources(context: Context, dexPath: String): Resources? {
        val assetManager = createAssetManager(dexPath)
        return if (assetManager != null) {
            createResources(context, assetManager)
        } else null
    }

    private fun createPackageInfo(context: Context, apkFilepath: String): PackageInfo? {
        val pm = context.packageManager
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm.getPackageArchiveInfo(
                apkFilepath,
                PackageManager.GET_ACTIVITIES or
                        PackageManager.GET_SERVICES or
                        PackageManager.GET_META_DATA
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pkgInfo
    }

}
