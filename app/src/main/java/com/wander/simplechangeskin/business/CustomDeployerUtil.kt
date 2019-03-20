package com.wander.simplechangeskin.business

import com.qiyi.video.reader.skin.skinDeployer.SkinDeployerFactory


/**
 * author wander
 * date 2019/3/20
 *
 */
object CustomDeployerUtil {
    fun registerCustomSkinDeployer() {
        SkinDeployerFactory.registerSkinDeployer(ShapeView.shapeColorName, ShapeColorDeployer())
    }
}