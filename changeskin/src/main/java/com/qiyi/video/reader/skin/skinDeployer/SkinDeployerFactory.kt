package com.qiyi.video.reader.skin.skinDeployer

import android.view.View

/**
 * author wander
 * date 2019/3/19
 *
 */
object SkinDeployerFactory {
    private val supportSkinDeployer = HashMap<String, ISkinResDeployer<*>>()
    const val BACKGROUND = "background"
    const val SRC = "src"
    const val TEXT_COLOR = "textColor"
    const val TEXT_STRING = "text"
    const val COLOR_LIST = "color_list"

    init {
        supportSkinDeployer[BACKGROUND] = BackgroundResDeployer()
        supportSkinDeployer[SRC] = ImageResDeployer()
        supportSkinDeployer[TEXT_COLOR] = TextColorResDeployer()
        supportSkinDeployer[TEXT_STRING] = TextStringResDeployer()
        supportSkinDeployer[COLOR_LIST] = TextColorStateListDeployer()
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

    fun isSupportAttr(attributeName: String): Boolean {
        return supportSkinDeployer.containsKey(attributeName)
    }


}