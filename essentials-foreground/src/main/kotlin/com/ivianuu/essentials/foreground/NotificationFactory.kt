/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.foreground

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

interface NotificationFactory {
    fun buildNotification(): Notification
}

abstract class AbstractNotificationFactory(
    private val context: Context
) : NotificationFactory {

    @TargetApi(26)
    protected abstract fun createChannel(): NotificationChannel

    final override fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService<NotificationManager>()!!.createNotificationChannel(
                createChannel()
            )
        }

        return buildNotification(context)
    }

    protected abstract fun buildNotification(context: Context): Notification
}
