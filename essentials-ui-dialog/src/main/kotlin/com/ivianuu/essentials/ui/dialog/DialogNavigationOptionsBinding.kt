/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactories
import com.ivianuu.essentials.ui.navigation.NavigationOptions
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.MapEntries
import kotlin.reflect.KClass

@Effect
annotation class DialogNavigationOptionsBinding<K> {
    companion object {
        @Suppress("UNCHECKED_CAST")
        @MapEntries
        inline fun <@Arg("K") reified K, T : Any?> bind(): NavigationOptionFactories = mapOf(
            K::class as KClass<out Key> to {
                NavigationOptions(
                    opaque = true,
                    transition = FadeStackTransition()
                )
            }
        )
    }
}
