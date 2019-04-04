package com.qiyi.video.reader.skin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.qiyi.video.reader.skin.skinDeployer.SkinDeployerFactory
import com.qiyi.video.reader.skin.utils.SkinConfig

/**
 * author wander
 * date 2019/4/3
 *
 */
class SkinInflaterFactory : LayoutInflater.Factory {
    private val TAG = SkinInflaterFactory::class.java.simpleName
    var mViewCreateFactory: LayoutInflater.Factory? = null

    companion object {
        /**
         * 用于通过LAYOUT_INFLATER_SERVICE获取的LayoutInflater设置factory
         * 仅能设置一次
         */
        fun setFactory(application: Application) {
            try {
                LayoutInflater.from(application).factory = SkinInflaterFactory()
            } catch (e: Exception) {
            }
        }

        /**
         * 需在activity的super.onCreate之前设置
         */
        fun setFactory(activity: Activity) {
            try {
                val inflater = activity.layoutInflater
                val factory = SkinInflaterFactory()
                if (activity is AppCompatActivity) {
                    //AppCompatActivity本身包含一个factory,将TextView等转换为AppCompatTextView.java, 参考：AppCompatDelegateImplV9.java
                    factory.mViewCreateFactory = LayoutInflater.Factory { name, context, attrs ->
                        activity.delegate.createView(null, name, context, attrs)
                    }
                }
                inflater.factory = factory
            } catch (e: Exception) {
            }
        }
    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        Log.d(TAG, "SkinInflaterFactory onCreateView(), create view name=$name  ")
        var view: View? = mViewCreateFactory?.onCreateView(name, context, attrs)
        if (isSupportSkin(attrs)) {
            if (view == null) {
                view = createView(context, name, attrs)
            }
            if (view != null) {
                parseAndSaveSkinAttr(attrs, view)
            }
        }

        return view
    }

    private fun parseAndSaveSkinAttr(attrs: AttributeSet, view: View) {
        val xmlSpecifiedAttrs = getXmlSpecifiedAttrs(attrs)?.split("//|")
        for (i in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(i)
            val attributeValue = attrs.getAttributeValue(i)
            Log.d(
                TAG,
                "parseSkinAttr attrName=$attributeName \tattrValue=$attributeValue \tview=${view.javaClass.simpleName}"
            )
            if (!SkinDeployerFactory.isSupportAttr(attributeName)) {
                continue
            }
            if (notAssignAttr(xmlSpecifiedAttrs, attributeName)) {
                continue
            }
            if (!attributeValue.startsWith("@")) {
                continue
            }

            try {
                val id = attributeValue.substring(1).toInt()
                val idSuc = SkinManager.setSkinViewResource(view, attributeName, id)
                if (!idSuc) {
                    //通过资源名获取对应的资源id
                    setAttributeFromName(attributeValue, view, attributeName)
                }
            } catch (e: Exception) {
                setAttributeFromName(attributeValue, view, attributeName)
            }

        }

    }

    /**
     * 处理attributeValue不为Id的可能
     */
    private fun setAttributeFromName(attributeValue: String, view: View, attributeName: String) {
        try {
            val dividerIndex = attributeValue.indexOf("/")
            val entryName = attributeValue.substring(dividerIndex + 1)
            val typeName = attributeValue.substring(1, dividerIndex)
            val idByName = view.context.resources.getIdentifier(entryName, typeName, view.context.packageName)
            SkinManager.setSkinViewResource(view, attributeName, idByName)
        } catch (e: Exception) {
        }
    }

    /**
     * 不是指定的换肤属性
     * @param xmlSpecifiedAttrs 未指定则全部替换
     */
    private fun notAssignAttr(xmlSpecifiedAttrs: List<String>?, attributeName: String): Boolean {
        if (xmlSpecifiedAttrs.isNullOrEmpty()) {
            return false
        }
        return !xmlSpecifiedAttrs.contains(attributeName)
    }


    private fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        try {
            val inflater = LayoutInflater.from(context)
            if (-1 == name.indexOf('.')) {
                if ("View" == name || "ViewStub" == name || "ViewGroup" == name) {
                    view = inflater.createView(name, "android.view.", attrs)
                }
                if (view == null) {
                    view = inflater.createView(name, "android.widget.", attrs)
                }
                if (view == null) {
                    view = inflater.createView(name, "android.webkit.", attrs)
                }
            } else {
                view = inflater.createView(name, null, attrs)
            }

        } catch (ex: Exception) {
            Log.e(TAG, "createView(), create view failed", ex)
            view = null
        }
        return view
    }

    //只有在xml中设置了View的属性skin:enable，才支持xml属性换肤
    private fun isSupportSkin(attrs: AttributeSet): Boolean {
        return attrs.getAttributeBooleanValue(SkinConfig.SKIN_XML_NAMESPACE, SkinConfig.ATTR_SKIN_ENABLE, false)
    }

    //获取xml中指定的换肤属性，比如：skin:attrs = "textColor|background", 假如为空，表示支持所有能够支持的换肤属性
    private fun getXmlSpecifiedAttrs(attrs: AttributeSet): String? {
        return attrs.getAttributeValue(SkinConfig.SKIN_XML_NAMESPACE, SkinConfig.SUPPORTED_ATTR_SKIN_LIST)
    }
}