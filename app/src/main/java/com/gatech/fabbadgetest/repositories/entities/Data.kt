package com.gatech.fabbadgetest.repositories.entities


import com.squareup.moshi.Json

data class Data(
    @Json(name = "messages")
    var messages: List<Message>
)