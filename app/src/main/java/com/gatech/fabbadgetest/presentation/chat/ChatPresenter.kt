package com.gatech.fabbadgetest.presentation.chat

import com.gatech.fabbadgetest.ViewProvider
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType.Companion.findByType
import com.gatech.fabbadgetest.domain.models.ChatBaseModel
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.domain.models.ModelHelper.Companion.generateChatMessageModel
import com.gatech.fabbadgetest.domain.models.ModelHelper.Companion.generateChatModels

interface ChatPresenter {
    fun takeView(view: ChatView)
    fun dropView()
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
    fun onBindSendViewHolder(holder: ViewProvider, position: Int)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int)
    fun onClickSend(text: String)
}

class ChatPresenterImpl : ChatPresenter {

    private var view: ChatView? = null
    private var chatList = mutableListOf<ChatBaseModel>()

    override fun takeView(view: ChatView) {
        this.view = view
        initData()
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

    override fun onClickSend(text: String) {
        if (text.isEmpty()) return
        chatList.add(generateChatMessageModel(text, ChatViewType.SENDER.type))

        val position = chatList.size - 1
        view?.notifyItemInserted(position) {
            view?.scrollToPosition(position)
        }
    }

    private fun initData() {
        chatList = generateChatModels(1500).toMutableList()
        view?.notifyDataSetChanged()
        view?.scrollToPosition(chatList.size - 1)
    }
}