package com.gatech.fabbadgetest.domain.models

import com.gatech.fabbadgetest.repositories.entities.Message

data class ChatMessagesModel(val messages: List<ChatMessageModel>)
data class ChatMessageModel(val message: String, override val type: Int) : ChatBaseModel {

    companion object {
        fun convertToModel(messageResponse: Message): ChatMessageModel {
            return ChatMessageModel(
                messageResponse.message,
                messageResponse.type
            )
        }
    }
}