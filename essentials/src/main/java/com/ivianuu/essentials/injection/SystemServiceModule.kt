/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.injection

import android.accessibilityservice.AccessibilityService
import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.content.pm.ShortcutManager
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.fingerprint.FingerprintManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.midi.MidiManager
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.BatteryManager
import android.os.Build
import android.os.DropBoxManager
import android.os.HardwarePropertiesManager
import android.os.PowerManager
import android.os.UserManager
import android.os.Vibrator
import android.os.health.SystemHealthManager
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import androidx.core.content.ContextCompat
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FactoryKind
import com.ivianuu.injekt.create
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.stdlibx.cast
import kotlin.reflect.KClass

/**
 * Module which binds all available system services
 */
val systemServiceModule = module {
    getSystemServices()
        .map { it.cast<KClass<Any>>() }
        .forEach { service ->
            add(
                Binding.create(
                    type = service,
                    kind = FactoryKind,
                    definition = {
                        ContextCompat.getSystemService(
                            get(),
                            service.java
                        )!!
                    }
                )
            )
        }
}

@Suppress("DEPRECATION")
private fun getSystemServices(): Set<KClass<*>> {
    val systemServices = mutableSetOf<KClass<*>>()

    if (Build.VERSION.SDK_INT >= 23) {
        systemServices.add(CarrierConfigManager::class)
        systemServices.add(FingerprintManager::class)
        systemServices.add(MidiManager::class)
        systemServices.add(NetworkStatsManager::class)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        systemServices.add(HardwarePropertiesManager::class)
        systemServices.add(SystemHealthManager::class)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        systemServices.add(ShortcutManager::class)
    }

    if (Build.VERSION.SDK_INT > 22) {
        systemServices.add(SubscriptionManager::class)
        systemServices.add(UsageStatsManager::class)
    }
    if (Build.VERSION.SDK_INT > 21) {
        systemServices.add(AppWidgetManager::class)
        systemServices.add(BatteryManager::class)
        systemServices.add(CameraManager::class)
        systemServices.add(JobScheduler::class)
        systemServices.add(LauncherApps::class)
        systemServices.add(MediaProjectionManager::class)
        systemServices.add(MediaSessionManager::class)
        systemServices.add(RestrictionsManager::class)
        systemServices.add(TelecomManager::class)
        systemServices.add(TvInputManager::class)
    }
    if (Build.VERSION.SDK_INT > 19) {
        systemServices.add(AppOpsManager::class)
        systemServices.add(CaptioningManager::class)
        systemServices.add(ConsumerIrManager::class)
        systemServices.add(PrintManager::class)
    }
    if (Build.VERSION.SDK_INT > 18) {
        systemServices.add(BluetoothManager::class)
    }
    if (Build.VERSION.SDK_INT > 17) {
        systemServices.add(DisplayManager::class)
        systemServices.add(UserManager::class)
    }
    if (Build.VERSION.SDK_INT > 16) {
        systemServices.add(InputManager::class)
        systemServices.add(MediaRouter::class)
        systemServices.add(NsdManager::class)
    }
    systemServices.add(AccessibilityService::class)
    systemServices.add(AccountManager::class)
    systemServices.add(ActivityManager::class)
    systemServices.add(AlarmManager::class)
    systemServices.add(AudioManager::class)
    systemServices.add(ClipboardManager::class)
    systemServices.add(ConnectivityManager::class)
    systemServices.add(DevicePolicyManager::class)
    systemServices.add(DownloadManager::class)
    systemServices.add(DropBoxManager::class)
    systemServices.add(InputMethodManager::class)
    systemServices.add(KeyguardManager::class)
    systemServices.add(LayoutInflater::class)
    systemServices.add(LocationManager::class)
    systemServices.add(NfcManager::class)
    systemServices.add(NotificationManager::class)
    systemServices.add(PowerManager::class)
    systemServices.add(SearchManager::class)
    systemServices.add(SensorManager::class)
    systemServices.add(StorageManager::class)
    systemServices.add(TelephonyManager::class)
    systemServices.add(TextServicesManager::class)
    systemServices.add(UiModeManager::class)
    systemServices.add(UsbManager::class)
    systemServices.add(Vibrator::class)
    systemServices.add(WallpaperManager::class)
    systemServices.add(WifiP2pManager::class)
    systemServices.add(WifiManager::class)
    systemServices.add(WindowManager::class)

    return systemServices
}