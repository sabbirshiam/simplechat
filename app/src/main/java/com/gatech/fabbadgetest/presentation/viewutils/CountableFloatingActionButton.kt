package com.gatech.fabbadgetest.presentation.viewutils

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Property
import android.view.animation.OvershootInterpolator
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import com.gatech.fabbadgetest.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.stateful.ExtendableSavedState
import kotlin.math.*

private val STATE_KEY = CountableFloatingActionButton::class.java.name + ".STATE"
private const val COUNT_STATE = "COUNT"

private const val NORMAL_MAX_COUNT = 99
private const val NORMAL_MAX_COUNT_TEXT = "99+"

private const val MINI_MAX_COUNT = 9
private const val MINI_MAX_COUNT_TEXT = "9+"

private const val TEXT_SIZE_DP = 11
private const val TEXT_PADDING_DP = 6
private val MASK_COLOR = Color.parseColor("#33000000") // Translucent black as mask color
private val ANIMATION_INTERPOLATOR = OvershootInterpolator()
private const val RIGHT_TOP_POSITION = 0
private const val LEFT_BOTTOM_POSITION = 1
private const val LEFT_TOP_POSITION = 2
private const val RIGHT_BOTTOM_POSITION = 3

/**
 * A [FloatingActionButton] subclass that shows a counter badge on right top corner.
 */
class CountableFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.floatingActionButtonStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {

    private val animationProperty = object : Property<CountableFloatingActionButton, Float>(Float::class.java, "animation") {

        override fun set(counterFab: CountableFloatingActionButton, value: Float) {
            animationFactor = value
            postInvalidateOnAnimation()
        }

        override fun get(counterFab: CountableFloatingActionButton): Float {
            return 0f
        }
    }

    private val textSize = TEXT_SIZE_DP * resources.displayMetrics.density
    private val textPadding = TEXT_PADDING_DP * resources.displayMetrics.density

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Style.FILL_AND_STROKE
        textSize = this@CountableFloatingActionButton.textSize
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }
    private val textBounds: Rect = run {
        val maxCountText =
            NORMAL_MAX_COUNT_TEXT
        val textBounds = Rect()
        textPaint.getTextBounds(maxCountText, 0, maxCountText.length, textBounds)
        textBounds
    }
    private val circleBounds = Rect()
    private val contentBounds = Rect()

    private val animationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    private var animationFactor = 1f
    private var animator = ObjectAnimator()
    private val isAnimating: Boolean
        get() = animator.isRunning
    private val isSizeMini: Boolean
        get() = size == SIZE_MINI
    private val badgePosition: Int
    private var countText: String = ""

    /**
     * The count value to show on badge starting from 0
     */
    var count: Int = 0
        set(@IntRange(from = 0) value) {
            if (value == field) return
            field = if (value > 0) value else 0

            updateCountText()
            if (ViewCompat.isLaidOut(this)) {
                startAnimation()
            }
        }

    init {
        val styledAttributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CountableFloatingActionButton, 0, 0
        )
        textPaint.color = styledAttributes.getColor(R.styleable.CountableFloatingActionButton_badgeTextColor, Color.WHITE)
        circlePaint.color = styledAttributes.getColor(R.styleable.CountableFloatingActionButton_badgeBackgroundColor, getDefaultBadgeColor())
        badgePosition = styledAttributes.getInt(
            R.styleable.CountableFloatingActionButton_badgePosition,
            RIGHT_TOP_POSITION
        )
        styledAttributes.recycle()

        updateCountText()
    }

    private fun updateCountText() {
        countText = if (isSizeMini) when {
            count > MINI_MAX_COUNT -> MINI_MAX_COUNT_TEXT
            else -> count.toString()
        }
        else when {
            count > NORMAL_MAX_COUNT -> NORMAL_MAX_COUNT_TEXT
            else -> count.toString()
        }
    }

    private fun getDefaultBadgeColor(): Int = run {
        val colorStateList = backgroundTintList
        if (colorStateList != null) {
            colorStateList.defaultColor
        } else {
            val background = background
            if (background is ColorDrawable) {
                background.color
            } else {
                circlePaint.color
            }
        }
    }.applyColorMask()

    private fun Int.applyColorMask() = ColorUtils.compositeColors(MASK_COLOR, this)

    /**
     * Increase the current count value by 1
     */
    fun increase() {
        count += 1
    }

    /**
     * Decrease the current count value by 1
     */
    fun decrease() {
        count = if (count > 0) count - 1 else 0
    }

    private fun startAnimation() {
        var start = 0f
        var end = 1f
        if (count == 0) {
            start = 1f
            end = 0f
        }

        if (isAnimating) animator.cancel()

        animator = ObjectAnimator.ofObject(
            this, animationProperty, null, start, end
        ).apply {
            interpolator =
                ANIMATION_INTERPOLATOR
            duration = animationDuration.toLong()
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateCircleBounds()
    }

    private fun calculateCircleBounds() {
        val circleRadius = max(textBounds.width(), textBounds.height()) / 2f + textPadding
        val circleEnd = (circleRadius * 2).toInt()
        if (isSizeMini) {
            val circleStart = (circleRadius / 2).toInt()
            circleBounds.set(circleStart, circleStart, circleEnd, circleEnd)
        } else {
            val circleStart = (circleRadius / 2).toInt()
            circleBounds.set(circleStart, circleStart, (circleRadius * 2).toInt(), (circleRadius * 2).toInt())
        }

//        getMeasuredContentRect(circleBounds)
//        calculateCounterCenter(circleBounds, counterPossibleCenter)
//
//        textPaint.getTextBounds(countText, 0, countText.length, textBounds)
//        val counterDiameter = max(textBounds.width(), textBounds.height()) + 2 * textPadding
//
//        val counterRight = min(counterPossibleCenter.x + counterDiameter / 2, circleBounds.right.toFloat())
//        val counterTop = max(counterPossibleCenter.y - counterDiameter / 2, circleBounds.top.toFloat())
//
//        circleBounds.set((counterRight - counterDiameter).toInt(), counterTop.toInt(), counterRight.toInt(), (counterTop + counterDiameter).toInt())
    }

    private fun calculateCounterCenter(inBounds: Rect, outPoint: PointF) {
        val radius = min(inBounds.width(), inBounds.height()) / 2f
        calculateCounterCenter(radius, outPoint)
        outPoint.x = inBounds.centerX() + outPoint.x
        outPoint.y = inBounds.centerY() + outPoint.y
    }

    private fun calculateCounterCenter(radius: Float, outPoint: PointF) =
        calculateCounterCenter(radius, (PI / 4).toFloat(), outPoint)

    private fun calculateCounterCenter(radius: Float, angle: Float, outPoint: PointF) {
        outPoint.x = textBounds.height() + radius * cos(angle)
        outPoint.y = (textBounds.width() - 0.5 * radius * sin(angle)).toFloat()

        //var x = h +       r*Math.cos(theta) ;
        //       var y = k - 0.5 * r*Math.sin(theta) ;
        //
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (count > 0 || isAnimating) {
            if (getContentRect(contentBounds)) {
                val newLeft: Int
                val newTop: Int
                when (badgePosition) {
                    LEFT_BOTTOM_POSITION -> {
                        newLeft = contentBounds.left
                        newTop = contentBounds.bottom - circleBounds.height()
                    }
                    LEFT_TOP_POSITION -> {
                        newLeft = contentBounds.left
                        newTop = contentBounds.top
                    }
                    RIGHT_BOTTOM_POSITION -> {
                        newLeft = contentBounds.left + contentBounds.width() - circleBounds.width()
                        newTop = contentBounds.bottom - circleBounds.height()
                    }
                    RIGHT_TOP_POSITION -> {
                        newLeft = contentBounds.left + contentBounds.width() - circleBounds.width()
                        newTop = contentBounds.top
                    }
                    else -> {
                        newLeft = contentBounds.left + contentBounds.width() - circleBounds.width()
                        newTop = contentBounds.top
                    }
                }
                circleBounds.offsetTo(newLeft, newTop)
            }
            val cx = circleBounds.centerX().toFloat()
            val cy = circleBounds.centerY().toFloat()
            val radius = circleBounds.width() / 2f * animationFactor
            // Solid circle
            canvas.drawCircle(cx, cy, radius, circlePaint)
           // canvas.drawRect(circleBounds, circlePaint)

            // oval positions
//            left = 100
//            top = 500
//            right = 600
//            bottom = 800
//            canvas.drawRoundRect(
//                circleBounds.toRectF(),
//                cx, cy,
//                circlePaint)
//
//            canvas.drawRoundRect(
//                circleBounds.width().toFloat(),
//                circleBounds.top.toFloat(),
//                circleBounds.right.toFloat(),
//                circleBounds.bottom.toFloat(),
//                4f, 4f,
//                circlePaint
//            )

            // Count text
            textPaint.textSize = textSize * animationFactor
            canvas.drawText(countText, cx, cy + textBounds.height() / 2f, textPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (superState is ExtendableSavedState) {
            superState.extendableStates.put(
                STATE_KEY, bundleOf(
                    COUNT_STATE to count))
        }
        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state !is ExtendableSavedState) return

        val bundle = state.extendableStates.get(STATE_KEY)
        count = bundle?.getInt(COUNT_STATE) ?: 0

        requestLayout()
    }
}