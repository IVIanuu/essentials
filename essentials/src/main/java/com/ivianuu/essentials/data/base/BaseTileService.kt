package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), CoroutineScope, Injectable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected val disposables = CompositeDisposable()

    private val job = Job()

    protected val listeningDisposables = CompositeDisposable()
    protected var listeningCoroutineScope = CancellableCoroutineScope()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        job.cancel()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        listeningCoroutineScope = CancellableCoroutineScope()
    }

    override fun onStopListening() {
        listeningDisposables.clear()
        listeningCoroutineScope.cancel()
        super.onStopListening()
    }
}