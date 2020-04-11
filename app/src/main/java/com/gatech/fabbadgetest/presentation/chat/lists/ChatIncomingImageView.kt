package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.google.android.material.shape.CornerFamily
import kotlinx.android.synthetic.main.chat_incoming_image_view.view.*
import java.net.URLDecoder


class ChatIncomingImageView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_incoming_image_view, this, true)
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
            .centerCrop()
            .apply(circleCropTransform())
            .into(personalImage)
        val radius = resources.getDimension(R.dimen.default_corner_radius)
        incomingImage.shapeAppearanceModel = incomingImage.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
        Glide.with(this)
            .load(data.imageUrl)
            .into(incomingImage)
    }
}