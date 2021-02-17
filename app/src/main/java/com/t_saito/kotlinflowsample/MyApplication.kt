package com.t_saito.kotlinflowsample

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // 通知チャンネル初期設定
        applicationContext.initNotificationChannel()
    }
}
