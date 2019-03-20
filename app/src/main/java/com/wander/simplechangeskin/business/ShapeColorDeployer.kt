package com.wander.simplechangeskin.business

import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.skinDeployer.ISkinResDeployer
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/3/20
 *
 */
class ShapeColorDeployer : ISkinResDeployer<ShapeView> {
    override fun deploy(view: ShapeView, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager) {
        if (skinAttribute.attrValueTypeName == SkinConfig.resTypeColor) {
            view.shapeColor = skinResourceManager.getColor(skinAttribute)
        }
    }
}