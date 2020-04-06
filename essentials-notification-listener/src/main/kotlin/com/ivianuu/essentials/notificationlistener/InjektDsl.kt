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

package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.KeyOverload
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.set

@QualifierMarker
annotation class NotificationComponents {
    companion object : Qualifier.Element
}

@KeyOverload
fun <T : NotificationComponent> ComponentBuilder.bindNotificationComponentIntoSet(
    componentKey: Key<T>
) {
    set<NotificationComponent>(NotificationComponents) { add(componentKey) }
}

internal fun ComponentBuilder.notificationComponentInjection() {
    set<NotificationComponent>(setQualifier = NotificationComponents)
}
