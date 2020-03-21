package com.gatech.fabbadgetest.domain.usecases

import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.repositories.message.MessageRepository

class GetMessages(private val messageRepository: MessageRepository) : UseCase() {

    fun execute(): ResponseValue {
        return messageRepository.getMessages().map { response ->
            ResponseValue(response.data.messages.map { message ->
                ChatMessageModel.convertToModel(message)
            })
        }
    }

    data class ResponseValue(val messages: List<ChatMessageModel>) : UseCase.ResponseValue
}