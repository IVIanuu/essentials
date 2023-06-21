package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.currentComposer

@Composable fun <T> withCompositionLocals(
  vararg values: ProvidedValue<*>,
  content: @Composable () -> T
): T {
  currentComposer.startProviders(values)
  val result = content()
  currentComposer.endProviders()
  return result
}
