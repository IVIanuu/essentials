package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.injekt.android.AppGivenScopeOwner
import com.ivianuu.injekt.scope.AppGivenScope

abstract class EsApp : Application(), AppGivenScopeOwner {
    override lateinit var appGivenScope: AppGivenScope
    override fun onCreate() {
        appGivenScope = buildAppGivenScope()
        super.onCreate()
    }
    protected abstract fun buildAppGivenScope(): AppGivenScope
}
