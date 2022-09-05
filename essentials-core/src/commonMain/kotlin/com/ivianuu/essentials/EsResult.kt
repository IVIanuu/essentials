/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse

typealias EsResult<V, E> = Result<V, E>

fun <V> EsResult<V, *>.getOrNull() = getOrElse { null }
