package com.gatech.fabbadgetest.presentation.chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.presentation.viewutils.addFragmentToActivity

class ChatActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun getIntent(context: Context) = Intent(context, ChatActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportFragmentManager.findFragmentById(R.id.containerFrame) ?: run {
            val fragment = ChatFragment.newInstance()
            addFragmentToActivity(supportFragmentManager, fragment, R.id.containerFrame)
        }
    }
}
