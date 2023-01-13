/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.content

import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun clipboardManager(context: Context): ClipboardManager =
    context.getSystemService(ClipboardManager::class.java)

  @Provide inline fun restrictionsManager(context: Context): RestrictionsManager =
    context.getSystemService(RestrictionsManager::class.java)
}
