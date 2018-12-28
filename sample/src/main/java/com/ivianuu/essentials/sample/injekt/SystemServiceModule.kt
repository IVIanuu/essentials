package com.ivianuu.essentials.sample.injekt

import android.accessibilityservice.AccessibilityService
import android.accounts.AccountManager
import android.app.*
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.*
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import androidx.core.content.ContextCompat
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import kotlin.reflect.KClass

fun systemServiceModule() = module {
    val systemServices = mutableSetOf<KClass<*>>()

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

    systemServices
        .map { it as KClass<Any> }
        .forEach { service ->
            factory(service) { ContextCompat.getSystemService(get(), service.java)!! }
        }
}