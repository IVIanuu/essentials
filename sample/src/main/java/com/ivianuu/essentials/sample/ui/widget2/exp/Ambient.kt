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

package com.ivianuu.essentials.sample.ui.widget2.exp

import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.InheritedWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

inline fun <reified T> Ambient(): Ambient<T> = Ambient(typeOf(Ambient::class, typeOf<T>()))

class Ambient<T>(valueType: Type<T>) {

    private val providerType = typeOf<Provider<T>>(
        Provider::class, valueType
    )

    fun of(context: BuildContext): T =
        context.ancestorInheritedElementForWidgetOfExactType(providerType)!!.value

    inner class Provider<T>(
        val value: T,
        child: Widget
    ) : InheritedWidget(child = child, type = providerType) {
        override fun updateShouldNotify(oldWidget: InheritedWidget): Boolean = true
    }
}