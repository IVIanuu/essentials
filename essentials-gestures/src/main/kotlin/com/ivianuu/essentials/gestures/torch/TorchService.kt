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

package com.ivianuu.essentials.gestures.torch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import androidx.core.app.NotificationCompat
import com.example.essentials.gestures.R
import com.ivianuu.essentials.util.StringProvider
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.work.EsService
import com.ivianuu.injekt.inject

/**
 * Handles the torch
 */
class TorchService : EsService() {

    private val cameraManager: CameraManager by inject()
    private val notificationManager: NotificationManager by inject()
    private val systemBuildInfo: SystemBuildInfo by inject()
    private val stringProvider: StringProvider by inject()
    private val toaster: Toaster by inject()
    private val torchManager: TorchManager by inject()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                ACTION_SYNC_STATE -> syncState()
                ACTION_TOGGLE_TORCH -> toggleTorch()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun toggleTorch() {
        tryAndToast {
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    tryAndToast {
                        cameraManager.unregisterTorchCallback(this)
                        cameraManager.setTorchMode(cameraId, !enabled)
                        updateState(!enabled)
                    }
                }

                override fun onTorchModeUnavailable(cameraId: String) {
                    tryAndToast {
                        cameraManager.unregisterTorchCallback(this)
                        toaster.toast(R.string.es_failed_to_toggle_torch)
                        updateState(false)
                    }
                }
            }, null)
        }
    }

    private fun syncState() {
        tryAndToast {
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    cameraManager.unregisterTorchCallback(this)
                    torchManager.setTorchState(enabled)
                }

                override fun onTorchModeUnavailable(cameraId: String) {
                    cameraManager.unregisterTorchCallback(this)
                    torchManager.setTorchState(false)
                }
            }, null)
        }
    }

    private fun updateState(enabled: Boolean) {
        if (enabled) {
            val contentIntent = Intent(this, TorchService::class.java).apply {
                action = ACTION_TOGGLE_TORCH
            }

            val contentPendingIntent = PendingIntent.getService(
                this,
                3, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
            )
                .setAutoCancel(true)
                .setContentTitle(stringProvider.getString(R.string.es_notif_title_torch))
                .setContentText(stringProvider.getString(R.string.es_notif_text_torch))
                .setSmallIcon(R.drawable.es_ic_torch_on)
                .setContentIntent(contentPendingIntent)
                .build()

            startForeground(NOTIFICATION_ID, notification)
        } else {
            stopForeground(true)
            stopSelf()
        }

        torchManager.setTorchState(enabled)
    }

    private fun createNotificationChannel() {
        if (systemBuildInfo.sdk >= 26) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                stringProvider.getString(R.string.es_notif_channel_torch),
                NotificationManager.IMPORTANCE_LOW
            ).let {
                notificationManager.createNotificationChannel(it)
            }
        }
    }

    private fun tryAndToast(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_failed_to_toggle_torch)
            updateState(false)
        }
    }

    companion object {
        private const val ACTION_SYNC_STATE = "sync_state"
        private const val ACTION_TOGGLE_TORCH = "toggle_torch"
        private const val NOTIFICATION_CHANNEL_ID = "torch"
        private const val NOTIFICATION_ID = 3

        internal fun toggleTorch(context: Context) {
            context.startService(
                Intent(context, TorchService::class.java).apply {
                    action = ACTION_TOGGLE_TORCH
                }
            )
        }

        internal fun syncState(context: Context) {
            context.startService(
                Intent(context, TorchService::class.java).apply {
                    action = ACTION_SYNC_STATE
                }
            )
        }
    }
}