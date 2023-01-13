/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.appwidget

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun appWidgetManager(context: Context): AppWidgetManager =
    context.getSystemService(AppWidgetManager::class.java)
}
