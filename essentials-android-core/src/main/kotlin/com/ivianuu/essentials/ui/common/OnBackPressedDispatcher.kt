package com.ivianuu.essentials.ui.common

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.Composable

// todo replace with OnBackPressedDispatcherOwner once available
@Composable
val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner
    get() = compositionActivity
