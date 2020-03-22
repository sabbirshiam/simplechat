package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.domain.models.ChatHeaderModel
import kotlinx.android.synthetic.main.chat_header_view.view.*
import java.net.URLDecoder

class ChatHeaderView: ConstraintLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_header_view, this, true)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun onBindData(data: ChatHeaderModel) {
        topHeaderMsg.text = data.message.toString()

        Glide.with(this)
            .load(URLDecoder.decode("https://pbs.twimg.com/profile_images/578558726971371521/TEZwnCCV_400x400.jpeg", "UTF-8"))
            .centerCrop()
            .apply(circleCropTransform())
            .into(topHeaderImg)
    }
}