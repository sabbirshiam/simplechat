package com.gatech.fabbadgetest.domain.usecases

import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.repositories.message.MessageRepository

class PostMessage(private val messageRepository: MessageRepository) : UseCase() {
    fun execute(message: String): ResponseValue {
        return messageRepository.postMessage(message).map { response ->
            ResponseValue(ChatMessageModel.convertToModel(response.message))
        }
    }

    data class ResponseValue(val message: ChatMessageModel) : UseCase.ResponseValue
}