package com.gatech.fabbadgetest.repositories.entities


import com.squareup.moshi.Json

data class MessageResponse(
    @Json(name = "message")
    var message: Message
)