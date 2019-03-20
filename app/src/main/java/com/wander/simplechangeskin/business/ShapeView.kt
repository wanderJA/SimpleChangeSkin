package com.wander.simplechangeskin.business

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.qiyi.video.reader.skin.SkinManager

/**
 * author wander
 * date 2019/3/20
 *
 */
class ShapeView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private val paint = Paint()

    companion object {
        const val shapeColorName = "shapeColor"
    }

    var shapeColor = Color.RED
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.isAntiAlias = false
    }

    fun setSkinShapeColor(resId: Int) {
        SkinManager.setSkinViewResource(this, shapeColorName, resId)
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = shapeColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

}