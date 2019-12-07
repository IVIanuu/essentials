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

package com.ivianuu.essentials.sample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.foreground.ForegroundComponent
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.int
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ApplicationScope
@Single
class ForegroundDispatcher(
    private val boxFactory: PrefBoxFactory,
    private val componentProvider: Provider<ForegroundTestComponent>,
    private val foregroundManager: ForegroundManager
) : AppService {

    private val box = boxFactory.int("box")

    init {
        box.asFlow()
            .onEach { count ->
                (0 until count).forEach { index ->
                    val component = foregroundManager.components
                        .filterIsInstance<ForegroundTestComponent>()
                        .firstOrNull { it.tag == index }
                        ?: componentProvider { parametersOf(index) }

                    foregroundManager.startForeground(component)
                }

                foregroundManager.components
                    .filterIsInstance<ForegroundTestComponent>()
                    .filter { it.tag >= count }
                    .forEach { foregroundManager.stopForeground(it) }
            }
            .launchIn(GlobalScope)
    }

    fun inc() {
        GlobalScope.launch {
            box.set(box.get() + 1)
        }
    }

    fun dec() {
        GlobalScope.launch {
            box.set(box.get() - 1)
        }
    }
}

@Factory
class ForegroundTestComponent(
    private val context: Context,
    private val notificationManager: NotificationManager,
    @Param val tag: Int
) : ForegroundComponent() {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "channel", "Channel", NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    override fun attach(manager: ForegroundManager) {
        super.attach(manager)
        d { "attach $tag" }
    }

    override fun detach() {
        super.detach()
        d { "detach $tag" }
    }

    override fun buildNotification() = NotificationCompat.Builder(context, "channel")
        .setSmallIcon(R.drawable.ic_settings)
        .setContentTitle("Foreground $tag")
        .setOngoing(true)
        .build()!!
}