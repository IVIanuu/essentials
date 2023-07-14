/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.ContentResolver
import android.content.pm.PackageManager
import com.ivianuu.injekt.Provide

@Provide object ContextModule {
  @Provide inline fun packageManager(appContext: AppContext): PackageManager =
    appContext.packageManager

  @Provide inline fun contentResolver(appContext: AppContext): ContentResolver =
    appContext.contentResolver
}
