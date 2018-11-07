package com.ivianuu.essentials.data.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection

/**
 * Base broadcast receiver
 */
abstract class BaseBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
    }
}