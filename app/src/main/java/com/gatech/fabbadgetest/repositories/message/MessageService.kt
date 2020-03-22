package com.gatech.fabbadgetest.repositories.message

import com.gatech.fabbadgetest.repositories.entities.MessageResponse
import com.gatech.fabbadgetest.repositories.entities.MessagesResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageService {
    /*
     * getSupportedCurrencies(access_key)
     *
     * parameter: access_key
     * response: json of list of supported currencies.
     */

    @GET("messages")
    fun getMessages(
        @Query("limit") limit: Int = 10
    ): Single<MessagesResponse>

    @POST("message")
    fun postMessage(
        @Body body: MultipartBody
    ): Single<MessageResponse>
}