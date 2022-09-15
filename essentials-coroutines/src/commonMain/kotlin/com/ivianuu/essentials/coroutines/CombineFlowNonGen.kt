/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

inline fun <reified T> combine(flows: Collection<Flow<T>>): Flow<List<T>> =
  combine(flows) { it.toList() }
