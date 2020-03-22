package com.gatech.fabbadgetest.domain.usecases

import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.repositories.message.MessageRepository
import io.reactivex.Single

class GetMessages(private val messageRepository: MessageRepository) : UseCase() {

    fun executeInitial(): Single<ResponseValue> {
        return messageRepository.getMessages(0).map { response ->
            ResponseValue(response.data.messages.map { message ->
                ChatMessageModel.convertToModel(message)
            })
        }
    }

    fun executeBefore(): Single<ResponseValue> {
        return messageRepository.getMessages(10).map { response ->
            ResponseValue(response.data.messages.map { message ->
                ChatMessageModel.convertToModel(message)
            })
        }
    }

    data class ResponseValue(val messages: List<ChatMessageModel>) : UseCase.ResponseValue
}