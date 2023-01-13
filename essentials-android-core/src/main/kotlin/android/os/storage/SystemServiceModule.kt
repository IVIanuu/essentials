/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.os.storage

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun storageManager(context: Context): StorageManager =
    context.getSystemService(StorageManager::class.java)
}
