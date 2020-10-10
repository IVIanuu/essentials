package com.ivianuu.essentials.foreground

import android.app.Notification
import kotlinx.coroutines.CoroutineScope

interface ForegroundJob {

    val id: Int
    val isActive: Boolean
    val notification: Notification

    val scope: CoroutineScope

    fun updateNotification(notification: Notification)

    fun stop()

}
