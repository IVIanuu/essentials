package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), Injectable {

    protected val disposables = CompositeDisposable()
    protected val listeningDisposables = CompositeDisposable()

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onStopListening() {
        listeningDisposables.clear()
        super.onStopListening()
    }
}