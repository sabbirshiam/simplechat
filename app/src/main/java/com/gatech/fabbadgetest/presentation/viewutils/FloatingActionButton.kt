package com.gatech.fabbadgetest.presentation.viewutils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.gatech.fabbadgetest.R

enum class FabSize {
    FAB_SIZE_NORMAL, FAB_SIZE_MINI;

    companion object {
        fun getByIndex(index: Int): FabSize {
            return when (index) {
                FAB_SIZE_MINI.ordinal -> FAB_SIZE_MINI
                else -> FAB_SIZE_NORMAL
            }
        }
    }
}

enum class FabIconPosition {
    FAB_ICON_START, FAB_ICON_TOP, FAB_ICON_END, FAB_ICON_BOTTOM;

    companion object {
        fun getByIndex(index: Int): FabIconPosition {
            return when (index) {
                FAB_ICON_TOP.ordinal -> FAB_ICON_TOP
                FAB_ICON_END.ordinal -> FAB_ICON_END
                FAB_ICON_BOTTOM.ordinal -> FAB_ICON_BOTTOM
                else -> FAB_ICON_START
            }
        }
    }
}

class FloatingActionButton : AppCompatTextView {
    var fabType = FabType.FAB_TYPE_CIRCLE
        set(value) {
            field = value
            buildView()
        }
    var fabSize =
        FabSize.FAB_SIZE_NORMAL
        set(value) {
            field = value
            buildView()
        }
    var fabElevation = 0f
        set(value) {
            field = value
            buildView()
        }
    var fabColor = 0
        set(value) {
            field = value
            buildView()
        }
    var fabIcon: Drawable? = null
        set(value) {
            field = value
            buildView()
        }
    var fabIconColor = 0
        set(value) {
            field = value
            buildView()
        }
    var fabIconPosition =
        FabIconPosition.FAB_ICON_START
        set(value) {
            field = value
            buildView()
        }
    var fabRippleColor = 0
        set(value) {
            field = value
            buildView()
        }

    var clipPath = Path()
    var textBgRect =  RectF()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        color = Color.RED
        typeface = Typeface.DEFAULT
    }

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initTypedArray(attrs)
        createTextParams()
    }

    private fun initTypedArray(attrs: AttributeSet?) {
        if (attrs == null) return

        val ta = context.theme.obtainStyledAttributes(attrs,
            R.styleable.FloatingActionButton, 0, 0)

        fabType = FabType.getByIndex(ta.getInt(R.styleable.FloatingActionButton_fabType, FabType.FAB_TYPE_CIRCLE.ordinal))
        fabSize =
            FabSize.getByIndex(
                ta.getInt(
                    R.styleable.FloatingActionButton_fabSizes,
                    FabSize.FAB_SIZE_NORMAL.ordinal
                )
            )
        fabElevation = ta.getDimension(
            R.styleable.FloatingActionButton_fabElevation, resources.getDimension(
                R.dimen.fab_default_elevation
            ))
        fabColor = ta.getColor(
            R.styleable.FloatingActionButton_fabColor, ContextCompat.getColor(context,
                R.color.colorAccent
            ))
        @DrawableRes val fabIconRes = ta.getResourceId(R.styleable.FloatingActionButton_fabIcon, -1)
        fabIcon = if (fabIconRes != -1) AppCompatResources.getDrawable(context, fabIconRes) else null
        fabIconColor = ta.getColor(
            R.styleable.FloatingActionButton_fabIconColor, ContextCompat.getColor(context,
                R.color.colorFabIcon
            ))
        fabIconPosition =
            FabIconPosition.getByIndex(
                ta.getInt(
                    R.styleable.FloatingActionButton_fabIconPosition,
                    FabIconPosition.FAB_ICON_START.ordinal
                )
            )
       // fabRippleColor = ta.getColor(R.styleable.FloatingActionButton_fabRippleColor, ContextCompat.getColor(context, R.color.colorPrimary))

        ta.recycle()
    }

    private fun buildView() {
        createPaddings()
        createElevation()
        createBackground()
        createIcons()
        createBadge()
    }

    private fun createPaddings() {
        val paddingSize = if (fabSize == FabSize.FAB_SIZE_MINI) resources.getDimensionPixelSize(
            R.dimen.fab_text_horizontal_margin_mini
        ) else resources.getDimensionPixelSize(R.dimen.fab_text_horizontal_margin_normal)
        setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.fab_text_horizontal_margin_mini)
    }

    private fun createElevation() {
        ViewCompat.setElevation(this, fabElevation)
    }

    private fun createBackground() {
        val background = AppCompatResources.getDrawable(context, when (fabType) {
            FabType.FAB_TYPE_SQUARE -> R.drawable.fab_square_bg
            FabType.FAB_TYPE_ROUNDED_SQUARE -> R.drawable.fab_rounded_square_bg
            else -> R.drawable.fab_circle_bg
        })?.mutate()?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mutate().colorFilter = BlendModeColorFilter(fabColor, BlendMode.SRC_IN)
            } else {
                mutate().setColorFilter(fabColor, PorterDuff.Mode.SRC_IN)
            }
        } ?: return

        val selectableBackground =
            createSelectableBackground(
                background,
                fabRippleColor
            )
        setBackground(LayerDrawable(arrayOf(background, selectableBackground)))
    }

    private fun createIcons() {
        if (fabIcon == null) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            return
        }

        val h = fabIcon!!.intrinsicHeight
        val w = fabIcon!!.intrinsicWidth
        fabIcon!!.setBounds(0, 0, w, h)
        fabIcon?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mutate().colorFilter = BlendModeColorFilter(fabIconColor, BlendMode.SRC_IN)
            } else {
                mutate().setColorFilter(fabIconColor, PorterDuff.Mode.SRC_IN)
            }
        }

        when (fabIconPosition) {
            FabIconPosition.FAB_ICON_START -> setCompoundDrawablesRelativeWithIntrinsicBounds(fabIcon, null, null, null)
            FabIconPosition.FAB_ICON_TOP -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, fabIcon, null, null)
            FabIconPosition.FAB_ICON_END -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, fabIcon, null)
            FabIconPosition.FAB_ICON_BOTTOM -> setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, fabIcon)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val radius = resources.getDimension(R.dimen.fab_rounded_radius)
        textBgRect.apply { left = 20f; top = -10f; right = measuredWidth.toFloat(); bottom = measuredHeight.toFloat() }
        clipPath.addRoundRect(textBgRect, radius, radius, Path.Direction.CW)
        canvas?.clipPath(clipPath)
        canvas?.drawRect(textBgRect, textPaint)
    }

    private fun createTextParams() {
        gravity = Gravity.CENTER
        val maxLength = 25
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = InputFilter.LengthFilter(maxLength)
        filters = fArray
        ellipsize = TextUtils.TruncateAt.END
    }

    fun createBadge() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val noText = text == null || text.isEmpty()

        if (noText) {
            val size = when (fabSize) {
                FabSize.FAB_SIZE_MINI -> resources.getDimensionPixelSize(
                    R.dimen.fab_size_mini
                )
                FabSize.FAB_SIZE_NORMAL -> resources.getDimensionPixelSize(
                    R.dimen.fab_size_normal
                )
            }
            setMeasuredDimension(size, size)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun hide() {
        ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val scale = it.animatedValue as Float
                this@FloatingActionButton.scaleX = scale
                this@FloatingActionButton.scaleY = scale
            }
            duration = 100
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.INVISIBLE
                }
            })
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
    }

    fun show() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val scale = it.animatedValue as Float
                this@FloatingActionButton.scaleX = scale
                this@FloatingActionButton.scaleY = scale
            }
            duration = 100
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    visibility = View.VISIBLE
                }
            })
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
    }
}