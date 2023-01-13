/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.view.accessibility

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun accessibilityManager(context: Context): AccessibilityManager =
    context.getSystemService(AccessibilityManager::class.java)

  @Provide inline fun captioningManager(context: Context): CaptioningManager =
    context.getSystemService(CaptioningManager::class.java)
}
