package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import kotlinx.android.synthetic.main.chat_outgoing_img_view.view.*
import java.net.URLDecoder

class ChatOutgoingImageView : ConstraintLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_outgoing_img_view, this, true)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun onBindData(data: ChatMessageModel) {

        val width = context.resources.getDimension(R.dimen.attach_image_width).toInt()
        val height = context.resources.getDimension(R.dimen.attach_image_height).toInt()
        Glide.with(this)
            .load(URLDecoder.decode("https://pbs.twimg.com/profile_images/578558726971371521/TEZwnCCV_400x400.jpeg", "UTF-8"))
            .override(width, height)
            .fitCenter()
            .dontTransform()
            .apply(RequestOptions().transform(RoundedCorners(50)))
            .into(outgoingImage)
    }
}