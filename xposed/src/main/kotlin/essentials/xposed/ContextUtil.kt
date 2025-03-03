/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.xposed

import android.app.*
import android.content.*

fun hookAppContextOnce(block: (Application) -> Unit) {
  hookAllMethods(Application::class, "onCreate") {
    before { block(`this`<Application>()) }
  }
}

fun hookSystemContextOnce(classLoader: ClassLoader, block: (Context) -> Unit) {
  hookAllConstructors(classLoader.getClass("com.android.server.am.ActivityManagerService")) {
    before { block(arg(0)) }
  }
}
