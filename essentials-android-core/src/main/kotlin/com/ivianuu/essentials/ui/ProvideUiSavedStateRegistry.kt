package com.ivianuu.essentials.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.savedinstancestate.UiSavedStateRegistry
import androidx.compose.runtime.savedinstancestate.UiSavedStateRegistryAmbient
import androidx.compose.ui.platform.ContextAmbient
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.injekt.FunBinding

typealias ProvideUiSavedStateRegistry = UiDecorator

@UiDecoratorBinding
@FunBinding
@Composable
fun ProvideUiSavedStateRegistry(children: @Composable () -> Unit) {
    val activity = ContextAmbient.currentOrNull as? ComponentActivity
    if (activity != null) {
        Providers(
            UiSavedStateRegistryAmbient provides UiSavedStateRegistry(emptyMap()) { true },
            children = children
        )
    }
}
