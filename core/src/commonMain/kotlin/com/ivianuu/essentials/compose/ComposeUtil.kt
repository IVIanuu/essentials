package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.resource.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Composable fun <T> StateFlow<T>.state(vararg keys: Any?): T =
  state(initial = value, keys = keys)

@Composable fun <T> Flow<T>.state(initial: T, vararg keys: Any?): T =
  state(initial, keys = keys) { collect { value = it } }

@Composable fun <T> Flow<T>.resourceState(vararg keys: Any?): Resource<T> =
  resourceState(keys = keys) { collect { value = it.success() } }

interface StateScope<T> : CoroutineScope {
  var value: T
}

@Composable fun <T> state(initial: T, vararg keys: Any?, block: suspend StateScope<T>.() -> Unit): T {
  val state = remember { mutableStateOf(initial) }
  LaunchedEffect(keys = keys) {
    block(
      object : StateScope<T>, CoroutineScope by this {
        override var value: T by state
      }
    )
  }
  return state.value
}

@Composable fun <T> resourceState(vararg keys: Any?, block: suspend StateScope<Resource<T>>.() -> Unit): Resource<T> =
  state<Resource<T>>(Resource.Loading, keys = keys) {
    catch { block(this) }
      .onLeft { value = it.error() }
  }

@Composable fun <T> StateFlow<T>.scopedState(vararg keys: Any?): T =
  scopedState(initial = value, keys = keys)

@Composable fun <T> Flow<T>.scopedState(initial: T, vararg keys: Any?): T =
  scopedState(initial, keys = keys) { collect { value = it } }

@Composable fun <T> Flow<T>.scopedResourceState(vararg keys: Any?): Resource<T> =
  scopedResourceState(keys = keys) { collect { value = it.success() } }

@Composable fun <T> scopedState(initial: T, vararg keys: Any?, block: suspend StateScope<T>.() -> Unit): T {
  val state = rememberScoped { mutableStateOf(initial) }
  LaunchedScopedEffect(keys = keys) {
    block(
      object : StateScope<T>, CoroutineScope by this {
        override var value: T by state
      }
    )
  }
  return state.value
}

@Composable fun <T> scopedResourceState(vararg keys: Any?, block: suspend StateScope<Resource<T>>.() -> Unit): Resource<T> =
  scopedState<Resource<T>>(Resource.Loading, keys = keys) {
    catch { block(this) }
      .onLeft { value = it.error() }
  }
