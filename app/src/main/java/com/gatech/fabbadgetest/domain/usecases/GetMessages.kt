package com.gatech.fabbadgetest.domain.usecases

import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.repositories.message.MessageRepository

class GetMessages(private val messageRepository: MessageRepository) : UseCase() {

    fun <T> execute(): List<ChatMessageModel> {
        messageRepository.getMessages()
        return listOf()
    }
}