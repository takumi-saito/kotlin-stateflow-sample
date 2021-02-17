package com.t_saito.kotlinflowsample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Coroutine listening for UI states
    private var job: Job? = null

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var jsonAdapter: JsonAdapter<User>

    var client: MyClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_emit).setOnClickListener {
            val user = User.Normal.randomUser()
            Timber.d("button emit user:$it")
            client?.emitUser(user)
        }

        userRepository.userStateFlow.onEach {
            Timber.d("userRepository.userStateFlow user:$it")
        }.launchIn(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()

        client = MyClient(
            this.applicationContext,
            object : MyClient.Listener {
                override fun onConnected(binder: MyService.MyBinder) {
                    lifecycleScope.launch {
                        binder.shouldShowSubActivityStateFlow.collect { shouldShowSubActivity ->
                            Timber.d("binder.shouldShowSubActivityStateFlow.collect shouldShowSubActivity:$shouldShowSubActivity")
                            binder.shouldShowSubActivityStateFlow.value = false
                            val user = binder.userStateFlow.value
                            Timber.d("binder.userStateFlow.value user:$user")
                            if (shouldShowSubActivity) {
//                                SubActivity.toSub(this@MainActivity, user)
                            }
                        }
                    }
                }

                override fun onDisConnected() {
                    client = null
                } },
            jsonAdapter
        )
        client?.doBindService()

        // Start collecting when the View is visible
        job = lifecycleScope.launch {
            userRepository.shouldShowSubActivityStateFlow.collect { shouldShowSubActivity ->
                Timber.d("userRepository.shouldShowSubActivityStateFlow.collect shouldShow:$shouldShowSubActivity")
                userRepository.shouldShowSubActivityStateFlow.value = false
                val user = userRepository.userStateFlow.value
                Timber.d("userRepository.userStateFlow.value user:$user")
                if (shouldShowSubActivity) {
                    SubActivity.toSub(this@MainActivity, user)
                }
            }
        }
    }

    override fun onStop() {
        // Stop collecting when the View goes to the background
        job?.cancel()

        client?.doUnBindService()
        client = null
        super.onStop()
    }

    companion object {
        fun toMain(context: Context?): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }
    }
}
