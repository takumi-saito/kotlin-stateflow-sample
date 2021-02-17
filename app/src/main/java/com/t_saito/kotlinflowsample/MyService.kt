package com.t_saito.kotlinflowsample

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MyService : Service(), CoroutineScope {

    private val binder = MyBinder()

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var jsonAdapter: JsonAdapter<User>

    val userMutableStateFlow: MutableStateFlow<User> = MutableStateFlow(User.Normal.EMPTY)
    val shouldShowSubActivityMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        when (action) {
            ACTION_EMIT -> {
                val jsonUser = intent.extras?.getString(KEY_USER) ?: ""
                val user = jsonAdapter.fromJson(jsonUser)
                user?.let { user ->
                    val pushUser = NotificationContent.PushUser(
                        user.firstName,
                        user.lastName,
                        user
                    )
                    startForeground(
                        pushUser.notificationId,
                        applicationContext.getNotification(pushUser)
                    )

                    Timber.d("onStartCommand action:%s jsonUser:%s", intent.action, jsonUser)
                    launch(Dispatchers.IO) {
                        delay(5000)
                        userRepository.emitUserStateFlow(user)

                        userMutableStateFlow.emit(user)

                        userRepository.shouldShowSubActivityStateFlow.emit(true)
                        shouldShowSubActivityMutableStateFlow.emit(true)
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        Timber.d("%s onBind", MyService::class.java.simpleName)
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Timber.d("%s onUnbind", MyService::class.java.simpleName)
        return super.onUnbind(intent)
    }

    inner class MyBinder : Binder() {
        val userStateFlow: StateFlow<User>
            get() = userMutableStateFlow
        val shouldShowSubActivityStateFlow: MutableStateFlow<Boolean>
            get() = shouldShowSubActivityMutableStateFlow
    }
    companion object {
        const val ACTION_EMIT = BuildConfig.APPLICATION_ID + "_ACTION_EMIT"
        const val KEY_USER = "key_user"
    }
}