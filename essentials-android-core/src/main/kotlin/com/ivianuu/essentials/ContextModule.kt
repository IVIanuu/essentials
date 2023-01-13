/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.content

import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide

object ContextModule {
  @Provide inline fun context(appContext: AppContext): Context = appContext
}