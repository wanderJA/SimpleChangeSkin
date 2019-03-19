package com.qiyi.video.reader.skin.attribute

import android.content.Context
import android.util.Log

/**
 * author wander
 * date 2019/3/19
 *
 */
class SkinAttribute(
    /***
     * 对应View的属性
     */
    var attrName: String,
    /***
     * 属性值对应的reference id值，类似R.color.XX,默认id
     */
    var resId: Int,
    /***
     * 属性值refrence id对应的类型，如R.color.XX，则此值为color
     */
    var attrValueTypeName: String,
    /***
     * 属性值refrence id对应的名称，如R.color.XX，则此值为"XX"
     */
    var attrValueRefName: String
) {
    companion object {

        fun parseSkinAttr(context: Context?, attrName: String, resId: Int): SkinAttribute? {
            if (context == null) {
                return null
            }
            var skinAttr: SkinAttribute? = null
            try {
                val attrValueName = context.resources.getResourceEntryName(resId)
                val attrValueType = context.resources.getResourceTypeName(resId)
                skinAttr = SkinAttribute(attrName, resId, attrValueType, attrValueName)
                Log.d("SkinAttribute",skinAttr.toString())
            } catch (ex: Exception) {
                Log.e("parseSkinAttr", " parseSkinAttr--- error happened ", ex)
            }

            return skinAttr
        }
    }

    override fun toString(): String {
        return "SkinAttribute(attrName='$attrName', resId=$resId, attrValueTypeName='$attrValueTypeName', attrValueRefName='$attrValueRefName')"
    }


}