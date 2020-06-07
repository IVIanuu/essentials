package com.ivianuu.essentials.ui.core

import androidx.compose.Composable
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

interface UiInitializer {
    @Composable
    fun apply(children: @Composable () -> Unit)
}

@Composable
fun UiInitializers(children: @Composable() () -> Unit) {
    val uiInitializers = inject<Map<KClass<out UiInitializer>, UiInitializer>>()
    val finalComposable: @Composable () -> Unit = uiInitializers.entries
        .map { (key, initializer) ->
            val function: @Composable (@Composable () -> Unit) -> Unit =
                { children ->
                    inject<Logger>().d("apply ui initializer $key")
                    initializer.apply(children)
                }
            function
        }
        .fold(children) { current: @Composable () -> Unit,
                          initializer: @Composable (@Composable () -> Unit) -> Unit ->
            @Composable { initializer(current) }
        }

    finalComposable()
}

@BindingEffect(ActivityComponent::class)
annotation class BindUiInitializer

@BindingEffectFunction(BindUiInitializer::class)
@Module
inline fun <reified T : UiInitializer> bindUiInitializer() {
    map<KClass<out UiInitializer>, UiInitializer> {
        put<T>(T::class)
    }
}

@Module
fun esUiInitializerModule() {
    installIn<ApplicationComponent>()
    map<KClass<out UiInitializer>, UiInitializer>()
}
