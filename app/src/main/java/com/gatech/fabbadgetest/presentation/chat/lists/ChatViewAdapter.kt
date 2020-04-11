package com.gatech.fabbadgetest.presentation.chat.lists

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gatech.fabbadgetest.ViewProvider
import java.lang.IllegalArgumentException

interface ContentCreator {
    fun createHeaderView(context: Context): View
    fun createIncomingTextView(context: Context): View
    fun createOutgoingTextView(context: Context): View
    fun createOutgoingImageView(context: Context): View
    fun createIncomingImageView(context: Context): View
}

interface ContentManager {
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
}

interface ContentBinder {
    fun onBindHeaderViewHolder(holder: ViewProvider, position: Int)
    fun onBindOutgoingTextViewHolder(holder: ViewProvider, position: Int)
    fun onBindOutgoingImageViewHolder(holder: ViewProvider, position: Int)
    fun onBindIncomingTextViewHolder(holder: ViewProvider, position: Int)
    fun onBindIncomingImageViewHolder(holder: ViewProvider, position: Int)
}

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

class ChatViewAdapter(
    private val contentCreator: ContentCreator,
    private val contentManager: ContentManager,
    private val bindListener: ContentBinder,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ChatViewType.HEADER.viewType ->
                ChatHeaderViewHolder(
                    contentCreator.createHeaderView(parent.context), onItemClickListener)
            ChatViewType.INCOMING_TEXT_VIEW.viewType ->
                ChatIncomingTextViewHolder(
                    contentCreator.createIncomingTextView(parent.context), onItemClickListener)
            ChatViewType.OUTGOING_TEXT_VIEW.viewType ->
                ChatOutgoingTextViewHolder(
                    contentCreator.createOutgoingTextView(parent.context), onItemClickListener)
            ChatViewType.OUTGOING_IMAGE_VIEW.viewType ->
                ChatOutgoingImageViewHolder(
                    contentCreator.createOutgoingImageView(parent.context), onItemClickListener)
            ChatViewType.INCOMING_IMAGE_VIEW.viewType ->
                ChatIncomingImageViewHolder(
                    contentCreator.createIncomingImageView(parent.context), onItemClickListener)
            else -> throw IllegalArgumentException()
        }

    override fun getItemCount(): Int = contentManager.getItemCount()
    override fun getItemViewType(position: Int): Int = contentManager.getItemViewType(position)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatHeaderViewHolder -> bindListener.onBindHeaderViewHolder(holder, position)
            is ChatOutgoingTextViewHolder -> bindListener.onBindOutgoingTextViewHolder(holder, position)
            is ChatIncomingTextViewHolder -> bindListener.onBindIncomingTextViewHolder(holder, position)
            is ChatOutgoingImageViewHolder -> bindListener.onBindOutgoingImageViewHolder(holder, position)
            is ChatIncomingImageViewHolder -> bindListener.onBindIncomingImageViewHolder(holder, position)
        }
    }

    class ChatHeaderViewHolder(view: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatHeaderView
        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
    }

    class ChatOutgoingTextViewHolder(view: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatOutgoingTextView
        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
    }

    class ChatIncomingTextViewHolder(view: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatIncomingTextView
        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
    }

    class ChatOutgoingImageViewHolder(view: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatOutgoingImageView
        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
    }

    class ChatIncomingImageViewHolder(view: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view), ViewProvider {
        override fun getView() = itemView as? ChatIncomingImageView
        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(adapterPosition) }
        }
    }
}