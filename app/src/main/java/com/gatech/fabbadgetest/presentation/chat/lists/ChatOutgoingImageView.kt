package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import kotlinx.android.synthetic.main.chat_outgoing_img_view.view.*
import timber.log.Timber


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
        var squaueUrl = "https://www5.lunapic.com/do-not-link-here-use-hosting-instead/158566666318389813?7298636021"
        var landscapeUrl = "https://vastphotos.com/files/uploads/photos/10592/lake-at-twilight-l.jpg"
        var portraitUrl = "https://images.squarespace-cdn.com/content/v1/524356bae4b04817bad65d5c/1555354881905-G6X8W8B0SZY2PFQJ1LTL/ke17ZwdGBToddI8pDm48kA-E3A5nZWA4inW_WqcGKdwUqsxRUqqbr1mOJYKfIPR7LoDQ9mXPOjoJoqy81S2I8PaoYXhp6HxIwZIk7-Mi3Tsic-L2IOPH3Dwrhl-Ne3Z2Qs5oYuN4O0sxBD_kPJGPstUprkcqQ60xRmZ9Cmzp5ccKMshLAGzx4R3EDFOm1kBS/casey_baugh_studio_painting.png"

        Timber.d("size:: ${Target.SIZE_ORIGINAL}")
        Glide.with(this)
            .load(portraitUrl)
            .fitCenter()
            .apply(RequestOptions().transform(RoundedCorners(50)))
            .into(outgoingImage)


        /*
        Glide.with(this)
            .asBitmap()
            .load(squaueUrl)
            .apply(RequestOptions().transform(RoundedCorners(12)))
            .into(object : CustomTarget<Bitmap>(width, 1) {
                // imageView width is 1080, height is set to wrap_content

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // bitmap will have size 1080 x 805 (original: 1571 x 1171)
                    Timber.d("resource width:: ${resource.width} height:: ${resource.height}")
                  //  outgoingImage.layoutParams.width = resource.width
                    outgoingImage.setImageBitmap(resource)
                }
            })
         */
    }
}