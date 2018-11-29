package com.ivianuu.essentials.injection

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
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
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
import android.os.BatteryManager
import android.os.DropBoxManager
import android.os.PowerManager
import android.os.UserManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import com.ivianuu.kommon.core.content.systemService
import dagger.Module
import dagger.Provides

/**
 * System service module
 */
@Module
object SystemServiceModule {

    @JvmStatic
    @Provides
    fun provideSubscriptionManager(context: Context) = context.systemService<SubscriptionManager>()

    @JvmStatic
    @Provides
    fun provideUsageStatsManager(context: Context) = context.systemService<UsageStatsManager>()

    @JvmStatic
    @Provides
    fun provideAppWidgetManager(context: Context) = context.systemService<AppWidgetManager>()

    @JvmStatic
    @Provides
    fun provideBatteryManager(context: Context) = context.systemService<BatteryManager>()

    @JvmStatic
    @Provides
    fun provideCameraManager(context: Context) = context.systemService<CameraManager>()

    @JvmStatic
    @Provides
    fun provideJobScheduler(context: Context) = context.systemService<JobScheduler>()

    @JvmStatic
    @Provides
    fun provideLauncherApps(context: Context) = context.systemService<LauncherApps>()

    @JvmStatic
    @Provides
    fun provideMediaProjectionManager(context: Context) =
        context.systemService<MediaProjectionManager>()

    @JvmStatic
    @Provides
    fun provideMediaSessionManager(context: Context) = context.systemService<MediaSessionManager>()

    @JvmStatic
    @Provides
    fun provideRestrictionsManager(context: Context) = context.systemService<RestrictionsManager>()

    @JvmStatic
    @Provides
    fun provideTelecomManager(context: Context) = context.systemService<TelecomManager>()

    @JvmStatic
    @Provides
    fun provideTvInputManager(context: Context) = context.systemService<TvInputManager>()

    @JvmStatic
    @Provides
    fun provideAppOpsManager(context: Context) = context.systemService<AppOpsManager>()

    @JvmStatic
    @Provides
    fun provideCaptioningManager(context: Context) = context.systemService<CaptioningManager>()

    @JvmStatic
    @Provides
    fun provideConsumerIrManager(context: Context) = context.systemService<ConsumerIrManager>()

    @JvmStatic
    @Provides
    fun providePrintManager(context: Context) = context.systemService<PrintManager>()

    @JvmStatic
    @Provides
    fun provideBluetoothManager(context: Context) = context.systemService<BluetoothManager>()

    @JvmStatic
    @Provides
    fun provideDisplayManager(context: Context) = context.systemService<DisplayManager>()

    @JvmStatic
    @Provides
    fun provideUserManager(context: Context) = context.systemService<UserManager>()

    @JvmStatic
    @Provides
    fun provideInputManager(context: Context) = context.systemService<InputManager>()

    @JvmStatic
    @Provides
    fun provideMediaRouter(context: Context) = context.systemService<MediaRouter>()

    @JvmStatic
    @Provides
    fun provideNsdManager(context: Context) = context.systemService<NsdManager>()

    @JvmStatic
    @Provides
    fun provideAccessibilityManager(context: Context) =
        context.systemService<AccessibilityManager>()

    @JvmStatic
    @Provides
    fun provideAccountManager(context: Context) = context.systemService<AccountManager>()

    @JvmStatic
    @Provides
    fun provideActivityManager(context: Context) = context.systemService<ActivityManager>()

    @JvmStatic
    @Provides
    fun provideAlarmManager(context: Context) = context.systemService<AlarmManager>()

    @JvmStatic
    @Provides
    fun provideAudioManager(context: Context) = context.systemService<AudioManager>()

    @JvmStatic
    @Provides
    fun provideClipboardManager(context: Context) = context.systemService<ClipboardManager>()

    @JvmStatic
    @Provides
    fun provideConnectivityManager(context: Context) = context.systemService<ConnectivityManager>()

    @JvmStatic
    @Provides
    fun provideDevicePolicyManagerManager(context: Context) =
        context.systemService<DevicePolicyManager>()

    @JvmStatic
    @Provides
    fun provideDownloadManager(context: Context) = context.systemService<DownloadManager>()

    @JvmStatic
    @Provides
    fun provideDropBoxManager(context: Context) = context.systemService<DropBoxManager>()

    @JvmStatic
    @Provides
    fun provideInputMethodManager(context: Context) = context.systemService<InputMethodManager>()

    @JvmStatic
    @Provides
    fun provideKeyguardManager(context: Context) = context.systemService<KeyguardManager>()

    @JvmStatic
    @Provides
    fun provideLayoutInflater(context: Context) = context.systemService<LayoutInflater>()

    @JvmStatic
    @Provides
    fun provideLocationManager(context: Context) = context.systemService<LocationManager>()

    @JvmStatic
    @Provides
    fun provideNfcManager(context: Context) = context.systemService<NfcManager>()

    @JvmStatic
    @Provides
    fun provideNotificationManager(context: Context) = context.systemService<NotificationManager>()

    @JvmStatic
    @Provides
    fun providePowerManager(context: Context) = context.systemService<PowerManager>()

    @JvmStatic
    @Provides
    fun provideSearchManager(context: Context) = context.systemService<SearchManager>()

    @JvmStatic
    @Provides
    fun provideSensorManager(context: Context) = context.systemService<SensorManager>()

    @JvmStatic
    @Provides
    fun provideStorageManager(context: Context) = context.systemService<StorageManager>()

    @JvmStatic
    @Provides
    fun provideTelephonyManager(context: Context) = context.systemService<TelephonyManager>()

    @JvmStatic
    @Provides
    fun provideTextServicesManager(context: Context) = context.systemService<TextServicesManager>()

    @JvmStatic
    @Provides
    fun provideUiModeManager(context: Context) = context.systemService<UiModeManager>()

    @JvmStatic
    @Provides
    fun provideUsbManager(context: Context) = context.systemService<UsbManager>()

    @JvmStatic
    @Provides
    fun provideVibrator(context: Context) = context.systemService<Vibrator>()

    @JvmStatic
    @Provides
    fun provideWallpaperManager(context: Context) = context.systemService<WallpaperManager>()

    @JvmStatic
    @Provides
    fun provideWifiP2pManager(context: Context) = context.systemService<WifiP2pManager>()

    @JvmStatic
    @Provides
    fun provideWifiManager(context: Context) = context.systemService<WifiManager>()

    @JvmStatic
    @Provides
    fun provideWindowManager(context: Context) = context.systemService<WindowManager>()

}