package com.qiyi.video.reader.skin.skinDeployer

import android.view.View

/**
 * author wander
 * date 2019/3/19
 *
 */
object SkinDeployerFactory {
    const val BACKGROUND = "background"
    const val IMAGE_RES = "image_res"
    const val TEXT_COLOR = "text_color"
    private val supportSkinDeployer = HashMap<String, ISkinResDeployer<*>>()

    init {
        supportSkinDeployer[BACKGROUND] = BackgroundResDeployer()
        supportSkinDeployer[IMAGE_RES] = ImageResDeployer()
        supportSkinDeployer[TEXT_COLOR] = TextColorResDeployer()

    }


    fun registerSkinDeployer(attrName: String, iSkinResDeployer: ISkinResDeployer<*>) {
        if (supportSkinDeployer.containsKey(attrName)) {
            throw IllegalArgumentException("attrName has bean register")
        }
        supportSkinDeployer[attrName] = iSkinResDeployer
    }

    fun getSkinDeployer(attrName: String): ISkinResDeployer<View>? {
        return supportSkinDeployer[attrName] as ISkinResDeployer<View>?
    }


}