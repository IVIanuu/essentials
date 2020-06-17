package com.ivianuu.essentials.foreground

import android.app.Notification

interface ForegroundJob {

    val id: Int
    val isActive: Boolean
    val notification: Notification

    fun updateNotification(notification: Notification)

    fun stop()

}
