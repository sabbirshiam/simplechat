package com.gatech.fabbadgetest.presentation.chat.lists

enum class ChatViewType(val viewType: Int, val type: Int) {
    HEADER(0,0),
    SENDER_TEXT_VIEW(1, 1),
    RECEIVER_TEXT_VIEW(2, 2),
    RECEIVER_IMAGE_VIEW(3, 3),
    SENDER_IMAGE_VIEW(4, 4);

    companion object {
        fun findByType(type: Int) = values().first { it.type == type }
    }
}