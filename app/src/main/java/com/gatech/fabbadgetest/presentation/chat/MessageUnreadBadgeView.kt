package com.gatech.fabbadgetest.presentation.chat

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.gatech.fabbadgetest.R
import kotlinx.android.synthetic.main.message_badge_layout.view.*

class MessageUnreadBadgeView(context: Context,
                             attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    var maxTextLength: Int = DEFAULT_MAX_TEXT_LENGTH
    var ellipsizeText: String = DEFAULT_ELLIPSIZE_TEXT

    val textView: TextView
    get() = tv_badge_text

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.message_badge_layout, this)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MessageUnreadBadge, 0, 0)
        try {
            val textColor = a.getColor(R.styleable.MessageUnreadBadge_android_textColor,
                DEFAULT_TEXT_COLOR.toInt())
            tv_badge_text.setTextColor(textColor)

            val textSize = a.getDimension(R.styleable.MessageUnreadBadge_android_textSize,
                dpToPx(DEFAULT_TEXT_SIZE))
            tv_badge_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

            maxTextLength = a.getInt(R.styleable.MessageUnreadBadge_muMaxTextLength,
                DEFAULT_MAX_TEXT_LENGTH)
        } finally {
            a.recycle()
        }
    }

    private var isVisible: Boolean
    get() = tv_badge_text.visibility == VISIBLE
    set(value) { tv_badge_text.visibility = if (value) View.VISIBLE else INVISIBLE }

    fun setBadgeBackground(@DrawableRes resourceId: Int){
        setBackgroundResource(resourceId)
    }

    fun setBadgeTextColor(@ColorRes color: Int) {
        tv_badge_text.currentTextColor
        tv_badge_text.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setText(text: String?) {
        val badgeText = when {
            text == null -> ""
            text.length > maxTextLength -> {
                setBadgeBackground(R.drawable.chat_badge_view_bg)
                ellipsizeText
            }
            else -> {
                setBadgeBackground(R.drawable.chat_badge_view_circle_bg)
                text
            }
        }
        tv_badge_text.text = badgeText
        isVisible = true
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
    }

    companion object {
        private const val DEFAULT_TEXT_COLOR = 0xFFFFFFFF
        private const val DEFAULT_TEXT_SIZE = 12
        private const val DEFAULT_MAX_TEXT_LENGTH = 2
        private const val DEFAULT_ELLIPSIZE_TEXT = "99+"
    }
}