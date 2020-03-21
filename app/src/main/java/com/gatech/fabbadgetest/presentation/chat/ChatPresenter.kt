package com.gatech.fabbadgetest.presentation.chat

import android.annotation.SuppressLint
import com.gatech.fabbadgetest.BaseSchedulerProvider
import com.gatech.fabbadgetest.ViewProvider
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType.Companion.findByType
import com.gatech.fabbadgetest.domain.models.ChatBaseModel
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.domain.models.ModelHelper.Companion.generateChatMessageModel
import com.gatech.fabbadgetest.domain.models.ModelHelper.Companion.generateChatModels
import com.gatech.fabbadgetest.domain.usecases.GetMessages
import com.gatech.fabbadgetest.domain.usecases.PostMessage
import timber.log.Timber

interface ChatPresenter {
    fun takeView(view: ChatView)
    fun dropView()
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
    fun onBindSendViewHolder(holder: ViewProvider, position: Int)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int)
    fun onClickSend(text: String)
    fun loadInitial()
}

class ChatPresenterImpl(
    private val getMessages: GetMessages,
    private val postMessage: PostMessage,
    private val scheduler: BaseSchedulerProvider
) : ChatPresenter {

    private var view: ChatView? = null
    private var chatList = mutableListOf<ChatBaseModel>()

    override fun takeView(view: ChatView) {
        this.view = view
    }

    override fun dropView() {
        view = null
    }

    override fun getItemCount(): Int = chatList.size
    override fun getItemViewType(position: Int): Int =
        findByType(chatList[position].type).viewType

    override fun onBindReceiveViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindReceiveViewHolder(holder, position, data)
    }

    override fun onBindSendViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindSendViewHolder(holder, position, data)
    }

    @SuppressLint("CheckResult")
    override fun onClickSend(text: String) {
        if (text.isEmpty()) return
        postMessage.execute(text)
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe(
                { response ->
                    chatList.add(ChatMessageModel(text, ChatViewType.SENDER.type))
                    chatList.add(response.message)
                    val position = chatList.size - 1
                    view?.notifyItemRangeInserted(chatList.size - 2, 2) {
                        view?.scrollToPosition(position)
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    @SuppressLint("CheckResult")
    override fun loadInitial() {
        getMessages.execute()
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe(
                { response ->
                    chatList = response.messages.toMutableList()
                    view?.notifyDataSetChanged()
                    view?.scrollToPosition(chatList.size - 1)
                }, {
                    Timber.e(it)
                }
            )
        //chatList = generateChatModels(1500).toMutableList()

    }
}