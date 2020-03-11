package com.ivianuu.essentials.android.ui.core

import androidx.compose.Composable
import androidx.compose.currentComposer
import com.ivianuu.essentials.util.sourceLocation

@Composable
inline fun pointInComposition() = currentComposer.joinKey(
    currentComposer.currentCompoundKeyHash,
    sourceLocation()
)