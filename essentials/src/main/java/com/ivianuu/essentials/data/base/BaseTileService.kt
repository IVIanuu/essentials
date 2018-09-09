package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.Job

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), Injectable {

    protected val disposables = CompositeDisposable()
    protected val job = Job()

    protected val listeningDisposables = CompositeDisposable()
    protected var listeningJob = Job()
        private set

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
        if (listeningJob.isCompleted) {
            listeningJob = Job()
        }
    }

    override fun onStopListening() {
        listeningDisposables.clear()
        listeningJob.cancel()
        super.onStopListening()
    }
}