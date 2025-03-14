/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import android.app.*
import android.content.*
import injekt.*

@Tag typealias AppContext = Context

@Provide object AppContextProviders {
  @Provide fun appContext(app: Application): @Service<AppScope> AppContext = app
}

fun appContext(scope: Scope<*> = inject): AppContext = service()
