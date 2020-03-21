package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import kotlinx.android.synthetic.main.chat_receiver_item_view.view.*

class ChatReceiverView : ConstraintLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_receiver_item_view, this, true)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun onBindData(data: ChatMessageModel) {
        message_body.text = data.message
    }
}