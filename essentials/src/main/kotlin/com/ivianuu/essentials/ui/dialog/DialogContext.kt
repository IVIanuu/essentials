/*
 * Copyright 2019 Manuel Wrage
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

import com.ivianuu.essentials.ui.effect.EffectContext
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.injekt.Component

data class DialogContext(val controller: EsDialogController) : EffectContext by EffectContext(),
    ContextAware by controller

fun DialogContext.pop(result: Any? = null) {
    navigator.pop(result)
}

val DialogContext.navigator: Navigator get() = controller.navigator
val DialogContext.component: Component get() = controller.component