/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import android.content.*
import android.content.pm.*
import injekt.*

@Provide object ContextModule {
  @Provide fun packageManager(appContext: AppContext): PackageManager =
    appContext.packageManager

  @Provide fun contentResolver(appContext: AppContext): ContentResolver =
    appContext.contentResolver
}
