package com.ivianuu.essentials.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.Composable
import androidx.ui.core.ContextAmbient

@Composable
val compositionActivity: ComponentActivity
    get() = ContextAmbient.current as ComponentActivity
