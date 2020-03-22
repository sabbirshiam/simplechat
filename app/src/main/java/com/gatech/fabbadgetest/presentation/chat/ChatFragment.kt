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
import androidx.recyclerview.widget.RecyclerView
import com.gatech.fabbadgetest.Injection
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.ViewProvider
import com.gatech.fabbadgetest.domain.models.ChatHeaderModel
import com.gatech.fabbadgetest.domain.models.ChatMessageModel
import com.gatech.fabbadgetest.presentation.chat.lists.*
import kotlinx.android.synthetic.main.fragment_chat.*


interface ChatView {
    fun onBindHeaderViewHolder(holder: ViewProvider, position: Int, data: ChatHeaderModel)
    fun onBindSendViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun notifyDataSetChanged()
    fun notifyItemInserted(position: Int, afterNotify: () -> Unit)
    fun notifyItemRangeInserted(position: Int, itemCount: Int, afterNotify: () -> Unit)
    fun scrollToPosition(position: Int)
    fun hideKeyboard()
    fun notifyItemInsert(position: Int, itemCount: Int, afterNotify: () -> Unit)
}

class ChatFragment : Fragment(), ChatView {

    private var presenter: ChatPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = Injection.provideChatPresenter()
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
        presenter?.loadInitial()
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
        //  (chatListView.layoutManager as LinearLayoutManager).stackFromEnd = true
        chatListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
             //   Timber.e("dx $dx, dy $dy")
                if (dy < 0) {
                    val layoutManager = (chatListView.layoutManager as LinearLayoutManager)
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    presenter?.loadHistory(visibleItemCount, pastVisibleItems)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
              //  Timber.e("state $newState, ")
            }
        })
    }

    override fun onBindHeaderViewHolder(
        holder: ViewProvider,
        position: Int,
        data: ChatHeaderModel
    ) {
        (holder.getView() as? ChatHeaderView)?.onBindData(data)
    }

    override fun onBindSendViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel) {
        (holder.getView() as? ChatOutgoingTextView)?.onBindData(data)
    }

    override fun onBindReceiveViewHolder(
        holder: ViewProvider,
        position: Int,
        data: ChatMessageModel
    ) {
        (holder.getView() as? ChatIncomingTextView)?.onBindData(data)
    }

    override fun notifyDataSetChanged() {
        chatListView?.adapter?.notifyDataSetChanged()
    }

    override fun notifyItemInserted(position: Int, afterNotify: () -> Unit) {
        chatListView?.adapter?.notifyItemInserted(position)
        afterNotify.invoke()
    }

    override fun notifyItemRangeInserted(position: Int, itemCount: Int, afterNotify: () -> Unit) {
        chatListView.adapter?.notifyItemRangeInserted(position, itemCount)
        afterNotify.invoke()
    }

    override fun notifyItemInsert(position: Int, itemCount: Int, afterNotify: () -> Unit) {
        chatListView.adapter?.notifyItemInserted(position)
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
                    override fun createHeaderView(context: Context): View {
                        return ChatHeaderView(context)
                    }

                    override fun createIncomingTextView(context: Context): View {
                        return ChatIncomingTextView(context)
                    }

                    override fun createOutgoingTextView(context: Context): View {
                        return ChatOutgoingTextView(context)
                    }
                },
                object : ContentManager {
                    override fun getItemCount(): Int = presenter?.getItemCount() ?: 0
                    override fun getItemViewType(position: Int): Int =
                        presenter?.getItemViewType(position) ?: -1
                },
                object : ContentBinder {
                    override fun onBindHeaderViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindHeaderViewHolder(holder, position)
                    }

                    override fun onBindOutgoingTextViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindSendViewHolder(holder, position)
                    }

                    override fun onBindIncomingTextViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindReceiveViewHolder(holder, position)
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}
