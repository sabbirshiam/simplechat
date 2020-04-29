package com.gatech.fabbadgetest.presentation.chat

import android.annotation.SuppressLint
import com.gatech.fabbadgetest.BaseSchedulerProvider
import com.gatech.fabbadgetest.ViewProvider
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType
import com.gatech.fabbadgetest.presentation.chat.lists.ChatViewType.Companion.findByType
import com.gatech.fabbadgetest.domain.models.ChatBaseModel
import com.gatech.fabbadgetest.domain.models.ChatHeaderModel
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.domain.usecases.GetMessages
import com.gatech.fabbadgetest.domain.usecases.PostMessage
import timber.log.Timber

interface ChatPresenter {
    fun takeView(view: ChatView)
    fun dropView()
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
    fun onBindHeaderViewHolder(holder: ViewProvider, position: Int)
    fun onBindOutgoingTextViewHolder(holder: ViewProvider, position: Int)
    fun onBindOutgoingImageViewHolder(holder: ViewProvider, position: Int)
    fun onBindIncomingTextViewHolder(holder: ViewProvider, position: Int)
    fun onClickSend(text: String)
    fun loadInitial()
    fun loadHistory(visibleItemCount: Int, pastVisibleItems: Int)
    fun onItemClick(position: Int)
    fun onBindIncomingImageViewHolder(holder: ViewProvider, position: Int)
}

class ChatPresenterImpl(
    private val getMessages: GetMessages,
    private val postMessage: PostMessage,
    private val scheduler: BaseSchedulerProvider
) : ChatPresenter {

    private var view: ChatView? = null
    private var chatList = mutableListOf<ChatBaseModel>()
    private var loading = false

    override fun takeView(view: ChatView) {
        this.view = view
    }

    override fun dropView() {
        view = null
    }

    override fun getItemCount(): Int = chatList.size
    override fun getItemViewType(position: Int): Int {
        return when(chatList[position]) {
            is ChatMessageModel -> {
                val model = chatList[position] as ChatMessageModel
                if(model.hasImageUrl()) return ChatViewType.INCOMING_IMAGE_VIEW.viewType
                else findByType(chatList[position].type).viewType
            }
            else -> findByType(chatList[position].type).viewType
        }
       // return findByType(chatList[position].type).viewType
    }

    override fun onBindHeaderViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatHeaderModel
        data ?: return
        view?.onBindHeaderViewHolder(holder, position, data)
    }

    override fun onBindIncomingTextViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindReceiveViewHolder(holder, position, data)
    }

    override fun onBindOutgoingTextViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindSendViewHolder(holder, position, data)
    }

    override fun onBindOutgoingImageViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindOutgoingImageViewHolder(holder, position, data)
    }

    override fun onBindIncomingImageViewHolder(holder: ViewProvider, position: Int) {
        val data = chatList[position] as? ChatMessageModel
        data ?: return
        view?.onBindIncomingImageViewHolder(holder, position, data)
    }

    override fun onItemClick(position: Int) {
        Timber.e("item Clicked:: ${(chatList[position] as ChatMessageModel).message}")
    }

    @SuppressLint("CheckResult")
    override fun onClickSend(text: String) {
        if (text.isEmpty()) return
        postMessage.execute(text)
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe(
                { response ->
                    chatList.add(ChatMessageModel(text, ChatViewType.OUTGOING_TEXT_VIEW.type, "", 100, 100))
                    chatList.add(response.message)
                    val position = chatList.size - 1
                    view?.notifyItemRangeInserted(chatList.lastIndex, 2) {
                        view?.scrollToPosition(position)
                    }
                }, {
                    Timber.e(it)
                }
            )
    }

    @SuppressLint("CheckResult")
    override fun loadInitial() {
        if (chatList.isNotEmpty()) return
        getMessages.executeLoadMessages()
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe(
                { response ->
                    Timber.e("initial response:: \n ${response.messages}")
                    chatList.takeLast(10)
                    chatList.addAll(response.messages.toMutableList())
                    view?.notifyDataSetChanged()
                    view?.scrollToPosition(chatList.size - 1)
                }, {
                    Timber.e(it)
                }
            )
    }

    @SuppressLint("CheckResult")
    override fun loadHistory(visibleItemCount: Int, pastVisibleItems: Int) {
        if (!loading && (chatList[0] !is ChatHeaderModel)) {
            loading = true
            getMessages.executeLoadMessages(10)
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribe(
                    { response ->
                        if (response.messages.isNotEmpty()) {
                            chatList.addAll(0, response.messages.toMutableList())
                            view?.notifyItemRangeInserted(0, response.messages.lastIndex) {
                                //loading = false
                            }
                        }
                        loading = false
                       // view?.scrollToPosition(chatList.size - 1)

                    }, {
                        Timber.e(it)
                    }
                )
        }
    }
}