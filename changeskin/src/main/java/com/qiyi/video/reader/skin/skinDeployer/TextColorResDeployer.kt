package com.qiyi.video.reader.skin.skinDeployer

import android.widget.TextView
import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/3/19
 *
 */
class TextColorResDeployer:ISkinResDeployer<TextView> {
    override fun deploy(view: TextView, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager) {
        if (SkinConfig.resTypeColor == skinAttribute.attrValueTypeName){
            view.setTextColor(skinResourceManager.getColor(skinAttribute))
        }
    }
}