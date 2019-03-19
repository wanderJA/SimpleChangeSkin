package com.qiyi.video.reader.skin.skinDeployer

import android.view.View
import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/3/19
 *
 */
class BackgroundResDeployer : ISkinResDeployer<View> {
    override fun deploy(view: View, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager) {
        when (skinAttribute.attrValueTypeName) {
            SkinConfig.resTypeColor -> view.setBackgroundColor(skinResourceManager.getColor(skinAttribute))
            SkinConfig.resTypeDrawable -> view.background = skinResourceManager.getDrawable(skinAttribute)
        }
    }
}