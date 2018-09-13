package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService(), CoroutineScope, Injectable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val disposables = CompositeDisposable()

    private val job = Job()

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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }
}