package com.wander.simplechangeskin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.qiyi.video.reader.skin.SkinManager
import com.wander.simplechangeskin.dummy.DummyContent

class MainActivity2 : AppCompatActivity(), ItemFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity2_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ItemFragment.newInstance(2))
                .commitNow()
        }
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.releaseViewImmediately(this)
    }
}
