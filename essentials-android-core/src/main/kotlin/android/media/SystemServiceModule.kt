/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package android.media

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun audioManager(context: Context): AudioManager =
    context.getSystemService(AudioManager::class.java)

  @Provide inline fun mediaRouter(context: Context): MediaRouter =
    context.getSystemService(MediaRouter::class.java)
}
