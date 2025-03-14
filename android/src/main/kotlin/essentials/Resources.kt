/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import android.graphics.drawable.*
import injekt.*

@Provide class Resources(private val appContext: AppContext) {
  fun loadIcon(id: Int): Icon = Icon.createWithResource(appContext, id)

  fun loadString(id: Int): String = appContext.getString(id)
}
