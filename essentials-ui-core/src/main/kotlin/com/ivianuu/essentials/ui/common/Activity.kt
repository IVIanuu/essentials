package com.ivianuu.essentials.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ContextAmbient

// todo remove
@Composable
val compositionActivity: ComponentActivity
    get() = ContextAmbient.current as ComponentActivity
