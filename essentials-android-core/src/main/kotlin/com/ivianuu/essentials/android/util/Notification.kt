package com.ivianuu.essentials.android.util

import android.annotation.SuppressLint
import android.app.Notification
import android.graphics.drawable.Icon

@SuppressLint("DiscouragedPrivateApi")
fun Notification.setSmallIcon(icon: Icon) {
    javaClass.getDeclaredMethod("setSmallIcon", Icon::class.java)
        .invoke(this, icon)
}