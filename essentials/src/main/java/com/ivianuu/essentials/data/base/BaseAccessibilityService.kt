package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.rx.disposableScopeProvider
import dagger.android.AndroidInjection

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService(), Injectable {

    val scopeProvider = disposableScopeProvider()

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        scopeProvider.dispose()
        super.onDestroy()
    }

}