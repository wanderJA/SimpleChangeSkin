package com.wander.simplechangeskin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.qiyi.video.reader.skin.SkinManager
import com.qiyi.video.reader.skin.utils.ISkinChangeObserver
import com.qiyi.video.reader.skin.utils.OnLoadSkinListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity(), ISkinChangeObserver {
    override fun onChanged(newSkinPath: String?) {
        changeSkinButton.isEnabled = SkinManager.pluginSkinPath == null
    }

    val tag = "MainActivity"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                SkinManager.setText(message, R.string.title_home)
                if (null == SkinManager.pluginSkinPath) {
                    changeSkin()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                if (null != SkinManager.pluginSkinPath) {
                    SkinManager.setText(message, R.string.title_dashboard)
                    SkinManager.restoreSkin()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                SkinManager.setText(message, R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        colorBg.setOnClickListener { toast("bg") }
        skinImage.setOnClickListener {
            toast("image")
            skinImage.isClickable = false
        }
        changeSkinButton.setOnClickListener { changeSkin() }
        SkinManager.setTextViewColor(message, R.color.lightColor)
        SkinManager.setBackground(message, R.color.colorBg)
        SkinManager.setText(message, R.string.title_home)
        SkinManager.setImageResource(skinImage, R.drawable.detail_relative_books)
        SkinManager.setBackground(changeSkinButton, R.drawable.circle_reader_bg_1_selecter)
        SkinManager.setTextViewColor(checkbox, R.color.check_color_list)

        shapeView.setSkinShapeColor(R.color.lightColor)
        shapeView.setOnClickListener { startActivity(Intent(this, MainActivity2::class.java)) }
        SkinManager.addObserver(this)
        Log.e(tag, "skinViewSize:${SkinManager.mSkinViewMap.size}")
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.removeObserver(this)
        if (isTaskRoot) {
            SkinManager.destroy()
        } else {
            SkinManager.releaseViewImmediately(this)
        }
    }

    private fun toast(s: String) {
        Log.d(tag, s)
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun changeSkin() {
        GlobalScope.launch {
            //将assets目录下的皮肤文件拷贝到data/data/.../cache目录下
            Log.d(tag, "thread ${Thread.currentThread().name}")
            val saveDir = cacheDir.absolutePath + "/skins"
            val saveFileName = "skin1.skin"
            val asset_dir = "skins/mylibrary-debug.apk"
            val file = File(saveDir + File.separator + saveFileName)
            //        if (!file.exists()) {
            AssetFileUtils.copyAssetFile(App.instance, asset_dir, saveDir, saveFileName)
            //        }
            val currentSkinPath = file.absolutePath
            SkinManager.loadNewSkin(currentSkinPath, object : OnLoadSkinListener {
                override fun onSuccess() {
                    toast("切换成功")
                }

                override fun onFail() {
                    toast("changeSkinFail")
                }
            })
        }
    }
}
