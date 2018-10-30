package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.ScopeCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.coroutines.cancelBy
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), CoroutineScope,
    Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = Job().cancelBy(scope)

    val listeningScope: Scope get() = _listeningScope
    private var _listeningScope = MutableScope()

    val listeningCoroutineScope: CoroutineScope get() = _listeningCoroutineScope
    private var _listeningCoroutineScope = ScopeCoroutineScope(listeningScope)
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
        _listeningCoroutineScope = ScopeCoroutineScope(_listeningScope)
    }

    override fun onStopListening() {
        _listeningScope.close()
        super.onStopListening()
    }
}