package com.t_saito.kotlinflowsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 通知チャンネルタイプ
 * 通知の種類毎に実装
 */
enum class NotificationChannelType(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int?,
    val importance: Int
) {
    PUSH_NOTICE(
        "1_user",
        R.string.notification_channel_name_notice,
        R.string.notification_channel_description_notice,
        NotificationManager.IMPORTANCE_LOW
    ),
}

private const val CHANNEL_GROUP_ID_CONTENTS: String = "contents"

/**
 * 通知チャンネル初期化
 */
fun Context.initNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // 通知チャンネル作成
        NotificationChannelType.values().forEach(::createNotificationChannel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.createNotificationChannelGroup() {
    val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    val channelGroupContents = NotificationChannelGroup(
        CHANNEL_GROUP_ID_CONTENTS,
        getString(R.string.notification_channel_group_name_contents)
    )
    if (!manager.notificationChannelGroups.contains(channelGroupContents)) {
        manager.createNotificationChannelGroup(channelGroupContents)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.createNotificationChannel(channelType: NotificationChannelType) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .createNotificationChannel(
            NotificationChannel(
                channelType.id,
                getString(channelType.nameRes),
                channelType.importance
            ).apply {
                if (channelType.descriptionRes != null) {
                    description = getString(channelType.descriptionRes)
                }
            }
        )
}

/**
 * 通知の表示
 */
fun Context.showNotification(
    content: NotificationContent
) {
    val notification = getNotification(content)
    NotificationManagerCompat.from(this)
        .notify(
            content.notificationId,
            notification
        )
}

/**
 * 通知の取得
 */
fun Context.getNotification(
    content: NotificationContent
): Notification {
    return when (content) {
        is NotificationContent.PushUser -> {
            NotificationCompat.Builder(this, content.channelType.id)
                .setContentTitle(content.title)
                .setContentText(content.text)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(content.title)
                        .bigText(content.text)
                )
                .setTicker(content.text)
                // .setFullScreenIntent(content.createPendingContentIntent(this), true)
                .setGroup(CHANNEL_GROUP_ID_CONTENTS)
                .setContentIntent(content.createPendingContentIntent(this))
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        }
    }
}
