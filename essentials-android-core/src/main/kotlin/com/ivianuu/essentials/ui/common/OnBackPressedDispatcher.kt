package com.ivianuu.essentials.ui.common

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.Composable

@Composable
val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner
    get() = compositionActivity
