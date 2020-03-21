package com.gatech.fabbadgetest.repositories.entities


import com.squareup.moshi.Json

data class MessagesResponse(
    @Json(name = "data")
    var `data`: Data,
    @Json(name = "message")
    var message: String,
    @Json(name = "status")
    var status: Boolean
)