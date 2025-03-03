/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.data

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*

@Stable interface DataStore<T> {
  val data: Flow<T>

  suspend fun updateData(transform: T.() -> T): T
}
