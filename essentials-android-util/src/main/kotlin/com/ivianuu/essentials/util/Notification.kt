/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.annotation.*
import android.app.*
import android.graphics.drawable.*

@SuppressLint("DiscouragedPrivateApi")
fun Notification.setSmallIcon(icon: Icon) {
  javaClass.getDeclaredMethod("setSmallIcon", Icon::class.java)
    .invoke(this, icon)
}
