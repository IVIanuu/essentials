package com.ivianuu.essentials.util

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.ivianuu.essentials.util.ext.intentFilterOf
import javax.inject.Inject

/**
 * Provides information about the battery
 */
class BatteryInfoProvider @Inject constructor(
    private val context: Context
) {

    val isCharging: Boolean
        get() {
            val intent = context.registerReceiver(
                null,
                intentFilterOf(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return false
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            return plugged == BatteryManager.BATTERY_PLUGGED_AC
                    || plugged == BatteryManager.BATTERY_PLUGGED_USB
                    || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

    val batteryLevel: Int
        get() {
            val batteryIntent = context.registerReceiver(
                null,
                intentFilterOf(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return -1
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            return if (level == -1 || scale == -1) {
                -1
            } else
                (level.toFloat() / scale.toFloat() * 100.0f).toInt()
        }
}