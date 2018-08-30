package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.util.rx.disposableScopeProvider
import dagger.android.AndroidInjection

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService() {

    val scopeProvider = disposableScopeProvider()
    val listeningScopeProvider = disposableScopeProvider()

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

    override fun onStopListening() {
        listeningScopeProvider.dispose()
        super.onStopListening()
    }

}