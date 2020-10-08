package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableContract
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy

@Composable
inline fun <T> rememberState(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember { mutableStateOf(init(), policy) }

@Composable
inline fun <T, V1> rememberState(
    v1: V1,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(v1) { mutableStateOf(init()) }

@Composable
inline fun <T, reified V1, reified V2> rememberState(
    v1: V1,
    v2: V2,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(v1, v2) { mutableStateOf(init()) }

@Composable
inline fun <T> rememberState(
    vararg inputs: Any?,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(*inputs) { mutableStateOf(init()) }