package com.csw.quickmvp.dialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.csw.quickmvp.utils.ScreenInfo

class LoadingView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var startTime = 0L
    private var started = false
    private var sizeChanged = true
    private var lineColor = Color.WHITE
    private var lineWidth = ScreenInfo.dp2Px(2f)
    private val linePaint = Paint()
    private val lineRectF = RectF()
    private var topDegree = -180
    private var bottomDegree = 0
    private var rotateAngle = 0f
    private var arc = 0f

    init {
        linePaint.color = lineColor
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = lineWidth.toFloat()
        linePaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        sizeChanged = true
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        sizeChanged = true
        super.setPadding(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (sizeChanged) {
            calcBounds()
            sizeChanged = false
        }
        if (started) {
            val spaceTime = System.currentTimeMillis() - startTime//间隔时间
            rotateAngle = spaceTime % 1000 / 1000f * 360//每秒旋转一周
            if (spaceTime >= 0 && spaceTime < 3000) {
                arc = spaceTime / 3000f * 160
            } else if (spaceTime >= 3000 && spaceTime < 6000) {
                arc = (6000 - spaceTime) / 3000f * 160
            } else {
                arc = 0f
            }
            postInvalidate()
        }
        arc = Math.max(1f, arc)
        if (!lineRectF.isEmpty && canvas != null) {
            canvas.save()
            canvas.rotate(rotateAngle, lineRectF.centerX(), lineRectF.centerY())
            linePaint.color = lineColor
            canvas.drawArc(lineRectF, topDegree.toFloat(), arc, false, linePaint)
            canvas.drawArc(lineRectF, bottomDegree.toFloat(), arc, false, linePaint)
            canvas.restore()
        }
    }

    private fun calcBounds() {
        lineRectF.set(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                width - paddingRight.toFloat(),
                height - paddingBottom.toFloat()
        )
        if (!lineRectF.isEmpty) {
            val dx = Math.max(0f, (lineRectF.width() - lineRectF.height()) / 2)
            val dy = Math.max(0f, (lineRectF.height() - lineRectF.width()) / 2)
            lineRectF.inset(dx, dy)
            lineRectF.inset(lineWidth.toFloat(), lineWidth.toFloat())
        }
    }

    fun start() {
        startTime = System.currentTimeMillis()
        arc = 0f
        rotateAngle = 0f
        started = true
        postInvalidate()
    }

    fun stop() {
        arc = 0f
        rotateAngle = 0f
        started = false
        postInvalidate()
    }

    fun setLineWidth(lineWidth: Int) {
        this.lineWidth = lineWidth
        linePaint.strokeWidth = lineWidth.toFloat()
        sizeChanged = true
        postInvalidate()
    }

    fun setLineColor(lineColor: Int) {
        this.lineColor = lineColor
        postInvalidate()
    }
}