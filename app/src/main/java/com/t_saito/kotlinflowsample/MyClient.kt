package com.t_saito.kotlinflowsample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonAdapter
import timber.log.Timber

class MyClient(
    private val applicationContext: Context,
    private val listener: Listener,
    private val jsonAdapter: JsonAdapter<User>,
    ) {

    interface Listener {
        fun onConnected(binder: MyService.MyBinder)
        fun onDisConnected()
    }

    private var isBound = false

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
            Timber.d("onServiceConnected")
            isBound = true
            listener.onConnected(iBinder as MyService.MyBinder)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("onServiceDisconnected")
            isBound = false
            listener.onDisConnected()
        }
    }

    fun doBindService() {
        val intent = Intent(applicationContext, MyService::class.java)
        applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun doUnBindService() {
        if (!isBound) return
        applicationContext.unbindService(connection)
        isBound = false
    }

    fun emitUser(user: User) {
        val args = Bundle().apply {
            putString(MyService.KEY_USER, jsonAdapter.toJson(user))
        }
        startService(MyService.ACTION_EMIT, args)
    }

    private fun startService(action: String, args: Bundle) {
        val serviceIntent = Intent(applicationContext, MyService::class.java)
        serviceIntent.putExtras(args)
        serviceIntent.action = action
        applicationContext.startForegroundService(serviceIntent)
    }
}