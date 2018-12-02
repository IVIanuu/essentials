package com.ivianuu.essentials.data.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.injectors.android.inject

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        inject(context)
    }
}