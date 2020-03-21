package com.gatech.fabbadgetest

import com.gatech.fabbadgetest.domain.usecases.GetMessages
import com.gatech.fabbadgetest.domain.usecases.PostMessage
import com.gatech.fabbadgetest.presentation.chat.ChatPresenter
import com.gatech.fabbadgetest.presentation.chat.ChatPresenterImpl
import com.gatech.fabbadgetest.repositories.message.MessageRepository
import com.gatech.fabbadgetest.repositories.message.MessageRepositoryImpl

object Injection {

    fun provideScheduleProvider(): BaseSchedulerProvider {
        return SchedulerProvider()
    }
    fun provideChatPresenter(): ChatPresenter {
        return ChatPresenterImpl(provideGetMessages(), providePostMessage(), provideScheduleProvider())
    }
    /**
     * Creates an instance of [GithubRepository] based on the [GithubService]
     */
    fun provideMessageRepository(): MessageRepository {
        return MessageRepositoryImpl()
    }

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService]
     */
    fun provideGetMessages(): GetMessages {
        return GetMessages(provideMessageRepository())
    }

    fun providePostMessage(): PostMessage {
        return PostMessage(provideMessageRepository())
    }
}