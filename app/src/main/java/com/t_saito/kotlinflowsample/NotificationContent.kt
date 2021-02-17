package com.t_saito.kotlinflowsample

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.io.Serializable

sealed class NotificationContent(
    open val title: String,
    open val text: String,
    val channelType: NotificationChannelType,
    open val notificationId: Int
) : Serializable {
    enum class NotificationType {
        PUSH_NOTICE_NOTIFICATION,
        ALARM_NOTIFICATION,
    }

    data class PushUser(
        override val title: String,
        override val text: String,
        val user: User
    ) : NotificationContent(
        title = title,
        text = text,
        channelType = NotificationChannelType.PUSH_NOTICE,
        notificationId = (if (user is User.Special) user.id + 1000 else user.id).toInt()
    ) {

        override fun createPendingContentIntent(context: Context): PendingIntent {
            val fullScreenIntent: Intent = MainActivity.toMain(context)
            return PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        override fun putExtrasTo(intent: Intent) {
            intent.apply {
                putExtra(
                    EXTRA_NOTIFICATION_ID,
                    NotificationType.PUSH_NOTICE_NOTIFICATION.ordinal
                )
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_USER, user)
            }
        }

        companion object {
            private const val EXTRA_WEB_URL = "EXTRA_WEB_URL"

            fun parse(intent: Intent): PushUser {
                return PushUser(
                    intent.getStringExtra(EXTRA_TITLE) ?: "",
                    intent.getStringExtra(EXTRA_TEXT) ?: "",
                    intent.getSerializableExtra(EXTRA_USER) as User
                )
            }
        }
    }


    abstract fun putExtrasTo(intent: Intent)
    abstract fun createPendingContentIntent(context: Context): PendingIntent

    companion object {
        const val EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"
        private const val EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_USER = "EXTRA_USER"

        fun parse(intent: Intent): NotificationContent {
            return when (intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)) {
                NotificationType.ALARM_NOTIFICATION.ordinal -> {
                    PushUser.parse(intent)
                }
                else -> throw NotImplementedError()
            }
        }
    }
}
