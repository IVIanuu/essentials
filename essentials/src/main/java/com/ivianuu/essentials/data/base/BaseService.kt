package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.rx.DisposableScopeProvider
import dagger.android.AndroidInjection

/**
 * Base service
 */
abstract class BaseService : Service(), Injectable {

    val scopeProvider = DisposableScopeProvider()

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        scopeProvider.dispose()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

}