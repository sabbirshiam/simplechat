package com.gatech.fabbadgetest.presentation.chat.lists

enum class ChatViewType(val viewType: Int, val type: Int) {
    SENDER(1, 1),
    RECEIVER(2, 2);

    companion object {
        fun findByType(type: Int) = values().first { it.type == type }
    }
}