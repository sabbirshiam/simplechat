package com.gatech.fabbadgetest.presentation.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.ViewProvider
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.presentation.chat.lists.*
import kotlinx.android.synthetic.main.fragment_chat.*

interface ChatView {
    fun onBindSendViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun notifyDataSetChanged()
    fun notifyItemInserted(position: Int, afterNotify: () -> Unit)
    fun scrollToPosition(position: Int)
    fun hideKeyboard()
}

class ChatFragment : Fragment(), ChatView {

    private var presenter: ChatPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ChatPresenterImpl(
            provideGetMessages(),
            providePostMessage(),
            provideScheduleProvider()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChatView()
        btnSend.setOnClickListener {
           // hideKeyboard() //enable this will causes ui glitch.
            val text = etInputText.text.toString()
            etInputText.text.clear()
            presenter?.onClickSend(text)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.takeView(this@ChatFragment)
    }

    override fun onPause() {
        super.onPause()
        presenter?.dropView()
    }

    private fun initChatView() {
        chatListView.adapter ?: initAdapter()
        (chatListView.layoutManager as LinearLayoutManager).stackFromEnd = true
    }

    override fun onBindSendViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel) {
        (holder.getView() as? ChatSenderTextView)?.onBindData(data)
    }

    override fun onBindReceiveViewHolder(
        holder: ViewProvider,
        position: Int,
        data: ChatMessageModel
    ) {
        (holder.getView() as? ChatReceiverView)?.onBindData(data)
    }

    override fun notifyDataSetChanged() {
        chatListView?.adapter?.notifyDataSetChanged()
    }

    override fun notifyItemInserted(position: Int, afterNotify: () -> Unit) {
        chatListView?.adapter?.notifyItemInserted(position)
        afterNotify.invoke()
    }

    override fun scrollToPosition(position: Int) {
        //chatListView.smoothScrollToPosition(position) // large dataset smoothScrollposition shows laggines..
        chatListView.scrollToPosition(position)
    }

    override fun hideKeyboard() {
        view?.let {
            val imm = ContextCompat.getSystemService(it.context, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun initAdapter() {
        chatListView.adapter =
            ChatViewAdapter(
                object : ContentCreator {
                    override fun createReceiverView(context: Context): View {
                        return ChatReceiverView(context)
                    }

                    override fun createSenderView(context: Context): View {
                        return ChatSenderTextView(context)
                    }
                },
                object : ContentManager {
                    override fun getItemCount(): Int = presenter?.getItemCount() ?: 0
                    override fun getItemViewType(position: Int): Int =
                        presenter?.getItemViewType(position) ?: -1
                },
                object : ContentBinder {
                    override fun onBindSendViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindSendViewHolder(holder, position)
                    }

                    override fun onBindReceiveViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindReceiveViewHolder(holder, position)
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}
