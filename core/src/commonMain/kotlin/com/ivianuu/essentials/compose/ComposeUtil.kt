package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.resource.*
import kotlinx.coroutines.flow.*

val <T> T.state: State<T>
  @Composable get() = rememberUpdatedState(this)

@Composable fun <T> StateFlow<T>.collect(vararg keys: Any?): T = collect(value, keys)

@Composable fun <T> Flow<T>.collect(initial: T, vararg keys: Any?): T =
  remember(keys = keys) { this }.collectAsState(initial).value

@Composable fun <T> Flow<T>.collectResource(vararg keys: Any?): Resource<T> =
  remember(keys) { flowAsResource() }.collect(Resource.Loading)

@Composable fun <T> collect(initial: T, vararg keys: Any?, block: suspend FlowCollector<T>.() -> Unit): T =
  remember(keys) { flow(block) }.collect(initial)

@Composable fun <T> collectResource(vararg keys: Any?, block: suspend FlowCollector<T>.() -> Unit): Resource<T> =
  remember(keys) { resourceFlow(block) }.collect(Resource.Loading)
