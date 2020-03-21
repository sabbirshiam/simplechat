package com.gatech.fabbadgetest.repositories.message

import com.gatech.fabbadgetest.repositories.RetrofitClientInstance
import com.gatech.fabbadgetest.repositories.entities.MessageResponse
import com.gatech.fabbadgetest.repositories.entities.MessagesResponse
import io.reactivex.Single
import okhttp3.MultipartBody

interface MessageRepository {
    fun getMessages(): Single<MessagesResponse>
    fun postMessage(message: String): Single<MessageResponse>
}

class MessageRepositoryImpl : MessageRepository {
    override fun getMessages(): Single<MessagesResponse> {
        return RetrofitClientInstance.messageService.getMessages()
    }

    override fun postMessage(message: String): Single<MessageResponse> {

        // if we want to create a builder
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("message", message)

        // MultipartBody.Part is used to send also the actual file name
        return RetrofitClientInstance.messageService.postMessage(builder.build())
    }
}