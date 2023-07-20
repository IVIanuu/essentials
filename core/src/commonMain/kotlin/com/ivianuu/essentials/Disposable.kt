package com.ivianuu.essentials

fun interface Disposable {
  fun dispose()

  companion object {
    val NoOp = Disposable {
    }
  }
}
