package com.qiyi.video.reader.skin.skinDeployer

import android.view.View
import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute

/**
 * author wander
 * date 2019/3/19
 *
 */
interface ISkinResDeployer<in T : View> {
    fun deploy(view: T, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager)
}