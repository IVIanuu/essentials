package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import com.ivianuu.essentials.util.coroutines.cancelCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

/**
 * Base service
 */
abstract class BaseService : Service(), CoroutineScope by CancellableCoroutineScope(), Injectable {

    protected val disposables = CompositeDisposable()

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

    override fun onBind(intent: Intent): IBinder? = null
}