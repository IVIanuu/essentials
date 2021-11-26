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

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.injekt.Tag

data class KeyUiOptions(
  val enterTransition: StackTransition? = null,
  val exitTransition: StackTransition? = null,
  val opaque: Boolean = false,
) {
  constructor(
    transition: StackTransition,
    opaque: Boolean = false,
  ) : this(transition, transition, opaque)
}

fun interface KeyUiOptionsFactory<K : Key<*>> : (K) -> KeyUiOptions

fun <K : Key<*>> noOpKeyUiOptionFactory() = KeyUiOptionsFactory<K> { defaultKeyUiOptions }

private val defaultKeyUiOptions = KeyUiOptions()
