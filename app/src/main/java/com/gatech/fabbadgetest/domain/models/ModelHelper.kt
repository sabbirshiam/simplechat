package com.gatech.fabbadgetest.domain.models

import java.util.*

class ModelHelper {
    companion object {
        fun generateChatModels(count: Int = 30): List<ChatBaseModel> {
            val list = mutableListOf<ChatMessageModel>()
            for (i in 0..count) {
                if (i % 2 == 0) {
                    list.add(
                        generateChatMessageModel(
                            "sender",
                            1,
                            ""
                        )
                    )
                } else {
                    list.add(
                        generateChatMessageModel(
                            "receiver",
                            2,
                            ""
                        )
                    )
                }
            }
            return list
        }

        fun generateChatMessageModel(message: String, type: Int, imageUrl: String): ChatMessageModel {
            val random = Random().nextLong()
            return ChatMessageModel(
                message,
                type,
                imageUrl,
                100,
                100
            )
        }
    }
}