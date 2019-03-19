package com.qiyi.video.reader.skin.skinDeployer

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.qiyi.video.reader.skin.SkinResourceManager
import com.qiyi.video.reader.skin.attribute.SkinAttribute
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/3/19
 *
 */
class ImageResDeployer : ISkinResDeployer<ImageView> {
    override fun deploy(view: ImageView, skinAttribute: SkinAttribute, skinResourceManager: SkinResourceManager) {
        var drawable: Drawable? = null
        when (skinAttribute.attrValueTypeName) {
            SkinConfig.resTypeColor -> {
                drawable = ColorDrawable(skinResourceManager.getColor(skinAttribute))
            }
            SkinConfig.resTypeDrawable -> drawable = skinResourceManager.getDrawable(skinAttribute)
        }
        if (drawable != null) {
            view.setImageDrawable(drawable)
        }
    }
}