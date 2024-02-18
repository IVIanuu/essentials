package com.ivianuu.essentials.compose

/*
import androidx.compose.runtime.*
import com.ivianuu.essentials.resource.*
import kotlinx.coroutines.flow.*

val <T> T.state: State<T>
  @Composable get() = rememberUpdatedState(this)

@Composable fun <T> produce(
  initial: T,
  vararg keys: Any?,
  block: suspend ProduceStateScope<T>.() -> Unit
): T = produceState(initial, keys = keys, block).value

@Composable fun <T> produceResource(
  vararg keys: Any?,
  block: suspend FlowCollector<T>.() -> Unit
): Resource<T> = produce<Resource<T>>(Resource.Loading, keys = keys) {
  resourceFlow { block() }
    .collect { value = it }
}

@Composable fun <T> StateFlow<T>.collect(vararg keys: Any?): T = collect(value, keys)

@Composable fun <T> Flow<T>.collect(initial: T, vararg keys: Any?): T =
  remember(keys) { this }.collectAsState(initial).value

@Composable fun <T> Flow<T>.collectResource(vararg keys: Any?): Resource<T> =
  flowAsResource().collect(Resource.Loading, keys)
*/