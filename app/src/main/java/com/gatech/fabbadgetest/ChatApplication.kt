package com.gatech.fabbadgetest

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}