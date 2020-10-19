package com.ivianuu.essentials.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import com.ivianuu.essentials.ui.common.RetainedObjects
import com.ivianuu.essentials.ui.common.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.core.currentOrNull

@UiDecoratorBinding
fun ProvideActivityRetainedObjects(): UiDecorator = { children ->
    val activity = ContextAmbient.currentOrNull as? ComponentActivity
    if (activity != null) {
        val retainedObjects = remember { RetainedObjects() }
        Providers(RetainedObjectsAmbient provides retainedObjects,
            children = children
        )
    }
}
