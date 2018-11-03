package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.asMainCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(),
    Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val listeningScope: Scope get() = _listeningScope
    private var _listeningScope = MutableScope()

    val listeningCoroutineScope get() = _listeningCoroutineScope
    private var _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        _listeningScope = MutableScope()
        _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()
    }

    override fun onStopListening() {
        _listeningScope.close()
        super.onStopListening()
    }
}