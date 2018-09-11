package com.ivianuu.essentials.data.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection

/**
 * Base broadcast receiver
 */
abstract class BaseBroadcastReceiver : BroadcastReceiver(), Injectable {

    override fun onReceive(context: Context, intent: Intent) {
        if (shouldInject) {
            AndroidInjection.inject(this, context)
        }
    }
}