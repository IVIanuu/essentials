package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.TileService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class BaseTileService : TileService(), Injectable, LifecycleOwner {

    protected val disposables = CompositeDisposable()
    protected val listeningDisposables = CompositeDisposable()

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        disposables.clear()
        super.onDestroy()
    }

    override fun onStopListening() {
        listeningDisposables.clear()
        super.onStopListening()
    }

    override fun onBind(intent: Intent): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return null
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle
}