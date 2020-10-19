package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.merge.MergeChildComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent

@MergeChildComponent
interface UiComponent

@UiDecoratorBinding
fun UiComponentUiDecorator(): UiDecorator = { children ->
    val activity = compositionActivity
    val uiComponent = remember {
        activity.activityComponent.mergeComponent<UiComponentFactoryOwner>()
            .uiComponentFactory()
    }
    Providers(
        UiComponentAmbient provides uiComponent,
        children = children
    )
}

@MergeInto(ActivityComponent::class)
interface UiComponentFactoryOwner {
    val uiComponentFactory: () -> UiComponent
}

val UiComponentAmbient = ambientOf<UiComponent> { error("No UiComponent installed") }
