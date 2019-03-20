package com.qiyi.video.reader.skin.skinDeployer

import android.widget.TextView
import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/3/20
 *
 */
class TextStringResDeployer:ISkinResDeployer<TextView> {
    override fun deploy(view: TextView, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager) {
      if (skinAttribute.attrValueTypeName == SkinConfig.resTypeString){
          view.text = skinResourceManager.getString(skinAttribute)
      }
    }
}