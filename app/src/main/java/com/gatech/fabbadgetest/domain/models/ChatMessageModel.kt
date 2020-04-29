package com.gatech.fabbadgetest.domain.models

import com.gatech.fabbadgetest.repositories.entities.Message

data class ChatMessagesModel(val messages: List<ChatMessageModel>)
data class ChatMessageModel(val message: String,
                            override val type: Int,
                            val imageUrl: String?,
                            val width: Int?,
                            val height: Int?) : ChatBaseModel {
    fun hasImageUrl() = (type == 2 && imageUrl?.isNotEmpty() ?: false)
    companion object {
        fun convertToModel(messageResponse: Message): ChatMessageModel {
            return ChatMessageModel(
                messageResponse.message,
                messageResponse.type,
                messageResponse.imageUrl,
                messageResponse.width,
                messageResponse.height
            )
        }
    }
}