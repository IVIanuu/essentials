package com.ivianuu.essentials.app

import android.app.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

abstract class EsApp : Application(), AppScopeOwner {
  override lateinit var appScope: AppScope
  override fun onCreate() {
    appScope = buildAppScope()
    super.onCreate()
  }

  protected abstract fun buildAppScope(): AppScope
}
