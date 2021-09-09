/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import com.ivianuu.essentials.cast

@Composable
fun <R> withCompositionContext(context: CompositionContext, block: @Composable () -> R): R {
  val compositionLocals = remember(context) {
    context.javaClass.declaredMethods
      .first { it.name == "getCompositionLocalScope\$runtime_release" }
      .invoke(context)
      .cast<Map<CompositionLocal<Any?>, State<Any?>>>()
      .map { (it.key as ProvidableCompositionLocal<Any?>).provides(it.value.value) }
      .toTypedArray()
  }
  currentComposer.startProviders(compositionLocals)
  val result = block()
  currentComposer.endProviders()
  return result
}
