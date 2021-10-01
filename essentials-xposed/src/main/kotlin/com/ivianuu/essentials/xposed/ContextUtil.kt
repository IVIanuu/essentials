package com.ivianuu.essentials.xposed

import android.app.Application
import android.content.Context

fun hookAppContextOnce(block: (Application) -> Unit) {
  hookAllMethods(Application::class, "onCreate") {
    before {
      block(`this`<Application>())
    }
  }
}

fun hookSystemContextOnce(classLoader: ClassLoader, block: (Context) -> Unit) {
  hookAllConstructors(
    classLoader.getClass("com.android.server.am.ActivityManagerService")
  ) {
    before {
      block(arg(0))
    }
  }
}