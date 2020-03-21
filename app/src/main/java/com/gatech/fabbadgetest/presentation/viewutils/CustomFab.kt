package com.gatech.fabbadgetest.presentation.viewutils

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.*

annotation class IntRange(val from: Int)
class CounterFloatingActionButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr) {

    private val textPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }
    private val tintPaint = Paint(ANTI_ALIAS_FLAG)

    private var countStr: String
    private var countMaxStr: String
    private var counterBounds: RectF = RectF()
    private var counterTextBounds: Rect = Rect()
    private var counterMaxTextBounds: Rect = Rect()
    private var counterPossibleCenter: PointF = PointF()

    private var fabBounds: Rect = Rect()

    var counterTextColor: Int
        get() = textPaint.color
        set(value) {
            val was = textPaint.color
            if (was != value) {
                textPaint.color = value
                invalidate()
            }
        }

    var counterTint: Int
        get() = tintPaint.color
        set(value) {
            val was = tintPaint.color
            if (was != value) {
                tintPaint.color = value
                invalidate()
            }
        }

    var counterTextSize: Float
        get() = textPaint.textSize
        set(value) {
            val was = textPaint.textSize
            if (was != value) {
                textPaint.textSize = value
                invalidate()
                requestLayout()
            }
        }

    var counterTypeface: Typeface?
        get() = textPaint.typeface
        set(value) {
            val was = textPaint.typeface
            if (was != value) {
                textPaint.typeface = value
                invalidate()
                requestLayout()
            }
        }

    var counterTextPadding: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                invalidate()
                requestLayout()
            }
        }

    var maxCount: Int = 9
        set(@IntRange(from= 1) value) {
            if (field != value) {
                field = value
                countMaxStr = "$value+"

                requestLayout()
            }
        }

    var count: Int = 0
        set(@IntRange(from = 0) value) {
            if (field != value) {
                field = value
                countStr = countStr(value)
                textPaint.getTextBounds(countStr, 0, countStr.length, counterTextBounds)

                invalidate()
            }
        }

    init {
        countStr = countStr(count)
        textPaint.getTextBounds(countStr, 0, countStr.length, counterTextBounds)
        countMaxStr = "$maxCount+"

        attrs?.let { initAttrs(attrs) }
    }

    @SuppressWarnings("ResourceType", "Recycle")
    private fun initAttrs(attrs: AttributeSet) {
//        context.obtainStyledAttributes(attrs, R.styleable.CounterFloatingActionButton).use {
//            counterTextPadding  = resources.getDimension(R.styleable.CounterFloatingActionButton_counterTextPadding, 0f)
//            counterTint = getColor(this.context, R.color.colorAccent)
//            context.obtainStyledAttributes(getResourceId(R.styleable.CounterFloatingActionButton_counterTextAppearance, R.style.Text_Primary), TEXT_APPEARANCE_SUPPORTED_ATTRS).use {
//                counterTextSize = resources.getDimension(R.dimen.fab_badge_text_size)
//                counterTextColor = getColor(this.context, R.color.colorPrimaryDark)
//                counterTypeface = resources.getTypeface(context, 2) ?: getTypeface(context, 3)
//            }
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateCounterBounds(counterBounds)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (count > 0) {
            canvas.drawCircle(counterBounds.centerX(),counterBounds.centerY(),counterBounds.width(), tintPaint)

            val textX = counterBounds.centerX() - counterTextBounds.width() / 2f - counterTextBounds.left
            val textY = counterBounds.centerY() + counterTextBounds.height() / 2f - counterTextBounds.bottom
            canvas.drawText(countStr, textX, textY, textPaint)
        }
    }

    private fun calculateCounterBounds(outRect: RectF) {
        getMeasuredContentRect(fabBounds)
        calculateCounterCenter(fabBounds, counterPossibleCenter)

        textPaint.getTextBounds(countMaxStr, 0, countMaxStr.length, counterMaxTextBounds)
        val counterDiameter = max(counterMaxTextBounds.width(), counterMaxTextBounds.height()) + 2 * counterTextPadding

        val counterRight = min(counterPossibleCenter.x + counterDiameter / 2, fabBounds.right.toFloat())
        val counterTop = max(counterPossibleCenter.y - counterDiameter / 2, fabBounds.top.toFloat())

        outRect.set(counterRight - counterDiameter, counterTop, counterRight, counterTop + counterDiameter)
    }

    private fun calculateCounterCenter(inBounds: Rect, outPoint: PointF) {
        val radius = min(inBounds.width(), inBounds.height()) / 2f
        calculateCounterCenter(radius, outPoint)
        outPoint.x = inBounds.centerX() + outPoint.x
        outPoint.y = inBounds.centerY() - outPoint.y
    }

    private fun calculateCounterCenter(radius: Float, outPoint: PointF) =
        calculateCounterCenter(radius, (PI / 4).toFloat(), outPoint)

    private fun calculateCounterCenter(radius: Float, angle: Float, outPoint: PointF) {
        outPoint.x = radius * cos(angle)
        outPoint.y = radius * sin(angle)
    }

    private fun countStr(count: Int) = if (count > maxCount) "$maxCount+" else count.toString()

    companion object {
        val TEXT_APPEARANCE_SUPPORTED_ATTRS = intArrayOf(android.R.attr.textSize, android.R.attr.textColor, androidx.appcompat.R.attr.fontFamily, android.R.attr.fontFamily)
    }
}