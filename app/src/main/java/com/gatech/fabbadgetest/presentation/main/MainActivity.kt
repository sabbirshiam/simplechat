package com.gatech.fabbadgetest.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.gatech.fabbadgetest.R
import com.gatech.fabbadgetest.presentation.chat.ChatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var count: Int by Delegates.observable(1) { _, _, newValue ->
            if (newValue > 9) {
                badgeCount.setBackgroundResource(R.drawable.chat_badge_view_bg)
            } else {
                badgeCount.setBackgroundResource(R.drawable.chat_badge_view_circle_bg)
            }
        }
        count = 1

        chat_fab.setOnClickListener { view ->
            if (count != 999) {
                count = count + 1
                if (count == 15) {
                    count = 999
                    badgeCount.text = count.toString().plus("+")
                } else {
                    badgeCount.text = count.toString()
                }
            }
            startActivity(ChatActivity.getIntent(this@MainActivity))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
