package com.ivianuu.essentials.app

import android.app.Application
import android.os.Looper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Enables async main thread schedulers
 */
class RxJavaAppInitializer : AppInitializer {
    override fun initialize(app: Application) {
        val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }
}