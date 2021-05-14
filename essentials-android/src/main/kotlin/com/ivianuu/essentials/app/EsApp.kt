package com.ivianuu.essentials.app

import android.app.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

abstract class EsApp : Application(), AppGivenScopeOwner {
  override lateinit var appGivenScope: AppGivenScope
  override fun onCreate() {
    appGivenScope = buildAppGivenScope()
    super.onCreate()
  }

  protected abstract fun buildAppGivenScope(): AppGivenScope
}
