package com.gatech.fabbadgetest.domain.usecases

import com.gatech.fabbadgetest.domain.models.ChatBaseModel
import com.gatech.fabbadgetest.domain.models.ChatHeaderModel
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType
import com.gatech.fabbadgetest.repositories.message.MessageRepository
import io.reactivex.Single

class GetMessages(private val messageRepository: MessageRepository) : UseCase() {

    fun executeInitial(): Single<ResponseValue> {
        return messageRepository.getMessages(0).map { response ->
            if(!response.status) ResponseValue(listOf(ChatHeaderModel("", "Header values", ChatViewType.HEADER.type)))
            else {
                ResponseValue(response.data.messages.map { message ->
                    ChatMessageModel.convertToModel(message)
                })
            }
        }
    }

    fun executeBefore(): Single<ResponseValue> {
        return messageRepository.getMessages(10).map { response ->
            ResponseValue(response.data.messages.map { message ->
                ChatMessageModel.convertToModel(message)
            })
        }
    }

    fun executeLoadMessages(limit: Int = 0): Single<ResponseValue> {
        return messageRepository.getMessages(limit).map { response ->
            if(!response.status) ResponseValue(listOf(ChatHeaderModel("", "Header values", ChatViewType.HEADER.type)))
            else {
                ResponseValue(response.data.messages.reversed().map { message ->
                    ChatMessageModel.convertToModel(message)
                })
            }
        }
    }

    data class ResponseValue(val messages: List<ChatBaseModel>) : UseCase.ResponseValue
}