package com.ivianuu.essentials.injection

import android.accounts.AccountManager
import android.app.*
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
import android.os.*
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
import com.ivianuu.essentials.util.ext.systemService
import dagger.Module
import dagger.Provides

/**
 * System service module
 */
@Module
object SystemServiceModule {

    @JvmStatic
    @Provides
    fun provideSubscriptionManager(app: Application) = app.systemService<SubscriptionManager>()

    @JvmStatic
    @Provides
    fun provideUsageStatsManager(app: Application) = app.systemService<UsageStatsManager>()

    @JvmStatic
    @Provides
    fun provideAppWidgetManager(app: Application) = app.systemService<AppWidgetManager>()

    @JvmStatic
    @Provides
    fun provideBatteryManager(app: Application) = app.systemService<BatteryManager>()

    @JvmStatic
    @Provides
    fun provideCameraManager(app: Application) = app.systemService<CameraManager>()

    @JvmStatic
    @Provides
    fun provideJobScheduler(app: Application) = app.systemService<JobScheduler>()

    @JvmStatic
    @Provides
    fun provideLauncherApps(app: Application) = app.systemService<LauncherApps>()

    @JvmStatic
    @Provides
    fun provideMediaProjectionManager(app: Application) =
        app.systemService<MediaProjectionManager>()

    @JvmStatic
    @Provides
    fun provideMediaSessionManager(app: Application) = app.systemService<MediaSessionManager>()

    @JvmStatic
    @Provides
    fun provideRestrictionsManager(app: Application) = app.systemService<RestrictionsManager>()

    @JvmStatic
    @Provides
    fun provideTelecomManager(app: Application) = app.systemService<TelecomManager>()

    @JvmStatic
    @Provides
    fun provideTvInputManager(app: Application) = app.systemService<TvInputManager>()

    @JvmStatic
    @Provides
    fun provideAppOpsManager(app: Application) = app.systemService<AppOpsManager>()

    @JvmStatic
    @Provides
    fun provideCaptioningManager(app: Application) = app.systemService<CaptioningManager>()

    @JvmStatic
    @Provides
    fun provideConsumerIrManager(app: Application) = app.systemService<ConsumerIrManager>()

    @JvmStatic
    @Provides
    fun providePrintManager(app: Application) = app.systemService<PrintManager>()

    @JvmStatic
    @Provides
    fun provideBluetoothManager(app: Application) = app.systemService<BluetoothManager>()

    @JvmStatic
    @Provides
    fun provideDisplayManager(app: Application) = app.systemService<DisplayManager>()

    @JvmStatic
    @Provides
    fun provideUserManager(app: Application) = app.systemService<UserManager>()

    @JvmStatic
    @Provides
    fun provideInputManager(app: Application) = app.systemService<InputManager>()

    @JvmStatic
    @Provides
    fun provideMediaRouter(app: Application) = app.systemService<MediaRouter>()

    @JvmStatic
    @Provides
    fun provideNsdManager(app: Application) = app.systemService<NsdManager>()

    // todo change method when support library fixes the issue
    @JvmStatic
    @Provides
    fun provideAccessibilityManager(app: Application) =
        app.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    @JvmStatic
    @Provides
    fun provideAccountManager(app: Application) = app.systemService<AccountManager>()

    @JvmStatic
    @Provides
    fun provideActivityManager(app: Application) = app.systemService<ActivityManager>()

    @JvmStatic
    @Provides
    fun provideAlarmManager(app: Application) = app.systemService<AlarmManager>()

    @JvmStatic
    @Provides
    fun provideAudioManager(app: Application) = app.systemService<AudioManager>()

    @JvmStatic
    @Provides
    fun provideClipboardManager(app: Application) = app.systemService<ClipboardManager>()

    @JvmStatic
    @Provides
    fun provideConnectivityManager(app: Application) = app.systemService<ConnectivityManager>()

    @JvmStatic
    @Provides
    fun provideDevicePolicyManagerManager(app: Application) =
        app.systemService<DevicePolicyManager>()

    @JvmStatic
    @Provides
    fun provideDownloadManager(app: Application) = app.systemService<DownloadManager>()

    @JvmStatic
    @Provides
    fun provideDropBoxManager(app: Application) = app.systemService<DropBoxManager>()

    @JvmStatic
    @Provides
    fun provideInputMethodManager(app: Application) = app.systemService<InputMethodManager>()

    @JvmStatic
    @Provides
    fun provideKeyguardManager(app: Application) = app.systemService<KeyguardManager>()

    @JvmStatic
    @Provides
    fun provideLayoutInflater(app: Application) = app.systemService<LayoutInflater>()

    @JvmStatic
    @Provides
    fun provideLocationManager(app: Application) = app.systemService<LocationManager>()

    @JvmStatic
    @Provides
    fun provideNfcManager(app: Application) = app.systemService<NfcManager>()

    @JvmStatic
    @Provides
    fun provideNotificationManager(app: Application) = app.systemService<NotificationManager>()

    @JvmStatic
    @Provides
    fun providePowerManager(app: Application) = app.systemService<PowerManager>()

    @JvmStatic
    @Provides
    fun provideSearchManager(app: Application) = app.systemService<SearchManager>()

    @JvmStatic
    @Provides
    fun provideSensorManager(app: Application) = app.systemService<SensorManager>()

    @JvmStatic
    @Provides
    fun provideStorageManager(app: Application) = app.systemService<StorageManager>()

    @JvmStatic
    @Provides
    fun provideTelephonyManager(app: Application) = app.systemService<TelephonyManager>()

    @JvmStatic
    @Provides
    fun provideTextServicesManager(app: Application) = app.systemService<TextServicesManager>()

    @JvmStatic
    @Provides
    fun provideUiModeManager(app: Application) = app.systemService<UiModeManager>()

    @JvmStatic
    @Provides
    fun provideUsbManager(app: Application) = app.systemService<UsbManager>()

    @JvmStatic
    @Provides
    fun provideVibrator(app: Application) = app.systemService<Vibrator>()

    @JvmStatic
    @Provides
    fun provideWallpaperManager(app: Application) = app.systemService<WallpaperManager>()

    @JvmStatic
    @Provides
    fun provideWifiP2pManager(app: Application) = app.systemService<WifiP2pManager>()

    @JvmStatic
    @Provides
    fun provideWifiManager(app: Application) = app.systemService<WifiManager>()

    @JvmStatic
    @Provides
    fun provideWindowManager(app: Application) = app.systemService<WindowManager>()

}