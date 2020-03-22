package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gatech.fabbadgetest.ViewProvider
import java.lang.IllegalArgumentException

interface ContentCreator {
    fun createHeaderView(context: Context): View
    fun createReceiverView(context: Context): View
    fun createSenderView(context: Context): View
}

interface ContentManager {
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
}

interface ContentBinder {
    fun onBindHeaderViewHolder(holder: ViewProvider, position: Int)
    fun onBindSendViewHolder(holder: ViewProvider, position: Int)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int)
}

class ChatViewAdapter(
    private val contentCreator: ContentCreator,
    private val contentManager: ContentManager,
    private val bindListener: ContentBinder
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ChatViewType.HEADER.viewType ->
                ChatHeaderViewHolder(
                    contentCreator.createHeaderView(parent.context)
                )
            ChatViewType.RECEIVER_TEXT_VIEW.viewType ->
                ChatReceiverViewHolder(
                    contentCreator.createReceiverView(parent.context)
                )
            ChatViewType.SENDER_TEXT_VIEW.viewType ->
                ChatSenderViewHolder(
                    contentCreator.createSenderView(parent.context)
                )
            else -> throw IllegalArgumentException()
        }

    override fun getItemCount(): Int = contentManager.getItemCount()
    override fun getItemViewType(position: Int): Int = contentManager.getItemViewType(position)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatHeaderViewHolder -> bindListener.onBindHeaderViewHolder(holder, position)
            is ChatSenderViewHolder -> bindListener.onBindSendViewHolder(holder, position)
            is ChatReceiverViewHolder -> bindListener.onBindReceiveViewHolder(holder, position)
        }
    }

    class ChatHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatHeaderView
    }

    class ChatSenderViewHolder(view: View) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatSenderTextView
    }

    class ChatReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatReceiverView
    }
}