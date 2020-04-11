package com.gatech.fabbadgetest.repositories.entities


import com.squareup.moshi.Json

data class Message(
    @Json(name = "message")
    var message: String,
    @Json(name = "type")
    var type: Int,
    @Json(name = "imageUrl")
    var imageUrl: String?
)