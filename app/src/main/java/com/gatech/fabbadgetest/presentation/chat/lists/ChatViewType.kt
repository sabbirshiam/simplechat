package com.gatech.fabbadgetest.presentation.chat.lists

enum class ChatViewType(val viewType: Int, val type: Int) {
    HEADER(0,0),
    OUTGOING_TEXT_VIEW(1, 1),
    INCOMING_TEXT_VIEW(2, 2),
    INCOMING_IMAGE_VIEW(3, 3),
    OUTGOING_IMAGE_VIEW(4, 4);

    companion object {
        fun findByType(type: Int) = values().first { it.type == type }
    }
}