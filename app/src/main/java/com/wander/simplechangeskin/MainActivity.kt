package com.wander.simplechangeskin

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.qiyi.video.reader.skin.SkinManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    val tag = "MainActivity"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                changeSkin()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                SkinManager.restoreSkin()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
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
        SkinManager.setImageResource(skinImage, R.drawable.detail_relative_books)
        SkinManager.setBackground(changeSkinButton, R.drawable.circle_reader_bg_1_selecter)

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
            val savefileName = "/skin1.skin"
            val asset_dir = "skins/mylibrary-debug.apk"
            val file = File(saveDir + File.separator + savefileName)
            //        if (!file.exists()) {
            AssetFileUtils.copyAssetFile(App.instance, asset_dir, saveDir, savefileName)
            //        }
            SkinManager.loadNewSkin(file.absolutePath)
            launch(Dispatchers.Main) {
                changeSkinButton.isSelected = SkinManager.pluginSkinPath == file.absolutePath
            }
        }
    }
}
