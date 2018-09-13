package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import com.ivianuu.essentials.util.coroutines.cancelCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), CoroutineScope by CancellableCoroutineScope(),
    Injectable {

    protected val disposables = CompositeDisposable()

    protected val listeningDisposables = CompositeDisposable()

    protected val listeningCoroutineScope: CoroutineScope
        get() = _listeningCoroutineScope
    private var _listeningCoroutineScope = CancellableCoroutineScope()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        cancelCoroutineScope()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        _listeningCoroutineScope = CancellableCoroutineScope()
    }

    override fun onStopListening() {
        listeningDisposables.clear()
        _listeningCoroutineScope.cancel()
        super.onStopListening()
    }
}