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
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.absoluteValue

interface ChatView {
    fun onBindHeaderViewHolder(holder: ViewProvider, position: Int, data: ChatHeaderModel)
    fun onBindSendViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun onBindReceiveViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun onBindOutgoingImageViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun onBindIncomingImageViewHolder(holder: ViewProvider, position: Int, data: ChatMessageModel)
    fun notifyDataSetChanged()
    fun notifyItemInserted(position: Int, afterNotify: () -> Unit)
    fun notifyItemRangeInserted(position: Int, itemCount: Int, afterNotify: () -> Unit)
    fun scrollToPosition(position: Int)
    fun hideKeyboard()
    fun notifyItemInsert(position: Int, itemCount: Int, afterNotify: () -> Unit)
}

class ChatFragment : Fragment(), ChatView {

    private var presenter: ChatPresenter? = null
    private var verticalScrollOffset = AtomicInteger(0)

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
        presenter?.loadInitial()
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
//        chatListView.addItemDecoration(HeaderItemDecoration(chatListView) { itemPosition ->
//           presenter?.isHeader(itemPosition) ?: false
//        })
        chatListView.setHasFixedSize(false)
      //  chatListView.adapter?.setHasStableIds(true)
        chatListView.adapter ?: initAdapter()
        //  (chatListView.layoutManager as LinearLayoutManager).stackFromEnd = true
        chatListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var state = AtomicInteger(RecyclerView.SCROLL_STATE_IDLE)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
              //  if (state.get() != RecyclerView.SCROLL_STATE_IDLE) {
                    verticalScrollOffset.getAndAdd(dy)
         //       }
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
//                state.compareAndSet(RecyclerView.SCROLL_STATE_IDLE, newState)
//                when (newState) {
//                    RecyclerView.SCROLL_STATE_IDLE -> {
//                        if (!state.compareAndSet(RecyclerView.SCROLL_STATE_SETTLING, newState)) {
//                            state.compareAndSet(RecyclerView.SCROLL_STATE_DRAGGING, newState)
//                        }
//                    }
//                    RecyclerView.SCROLL_STATE_DRAGGING -> {
//                        state.compareAndSet(RecyclerView.SCROLL_STATE_IDLE, newState)
//                    }
//                    RecyclerView.SCROLL_STATE_SETTLING -> {
//                        state.compareAndSet(RecyclerView.SCROLL_STATE_DRAGGING, newState)
//                    }
//                }
            }
        })

        chatListView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            val y = oldBottom - bottom
            chatListView.scrollBy(0, bottom + 100)
//            if (y.absoluteValue > 0) {
//                chatListView.post {
//                    if (y > 0 || verticalScrollOffset.get().absoluteValue >= y.absoluteValue) {
//                        chatListView.scrollBy(0, bottom + 100)
//                    } else {
//                        chatListView.scrollBy(0, verticalScrollOffset.get())
//                    }
//                }
//            }
        }
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

    override fun onBindOutgoingImageViewHolder(
        holder: ViewProvider,
        position: Int,
        data: ChatMessageModel
    ) {
        (holder.getView() as? ChatOutgoingImageView)?.onBindData(data)
    }

    override fun onBindIncomingImageViewHolder(
        holder: ViewProvider,
        position: Int,
        data: ChatMessageModel
    ) {
        (holder.getView() as? ChatIncomingImageView)?.onBindData(data)
    }

    override fun notifyDataSetChanged() {
        chatListView?.adapter?.notifyDataSetChanged()
    }

    override fun notifyItemInserted(position: Int, afterNotify: () -> Unit) {
        chatListView?.adapter?.notifyItemInserted(position)
        afterNotify.invoke()
    }

    override fun notifyItemRangeInserted(position: Int, itemCount: Int, afterNotify: () -> Unit) {
        chatListView.itemAnimator = null
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

                    override fun createOutgoingImageView(context: Context): View {
                        return ChatOutgoingImageView(context).apply {
                            setOnClickListener { Timber.e(" imageview:: $") }
                        }
                    }

                    override fun createIncomingImageView(context: Context): View {
                        return ChatIncomingImageView(context).apply {
                            setOnClickListener { Timber.e(" imageview:: $") }
                        }
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
                        presenter?.onBindOutgoingTextViewHolder(holder, position)
                    }

                    override fun onBindOutgoingImageViewHolder(holder: ViewProvider, position: Int
                    ) {
                        presenter?.onBindOutgoingImageViewHolder(holder, position)
                    }

                    override fun onBindIncomingTextViewHolder(holder: ViewProvider, position: Int) {
                        presenter?.onBindIncomingTextViewHolder(holder, position)
                    }

                    override fun onBindIncomingImageViewHolder(
                        holder: ViewProvider,
                        position: Int
                    ) {
                        presenter?.onBindIncomingImageViewHolder(holder, position)
                    }
                },
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        Timber.e("Item position:: $position")
                        presenter?.onItemClick(position)
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}
