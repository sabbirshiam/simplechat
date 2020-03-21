package com.gatech.fabbadgetest.domain.models

data class ChatMessageModel(val message: String, override val type: Int) : ChatBaseModel