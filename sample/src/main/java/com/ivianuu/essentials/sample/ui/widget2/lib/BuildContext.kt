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

package com.ivianuu.essentials.sample.ui.widget2.lib

import kotlin.reflect.KClass

interface BuildContext {

    val widget: Widget
    val owner: BuildOwner?

    fun inheritFromElement(ancestor: InheritedElement): InheritedWidget

    fun <T : InheritedWidget> inheritFromWidgetOfExactType(type: KClass<T>): T?

    fun <T : InheritedWidget> ancestorInheritedElementForWidgetOfExactType(
        type: KClass<T>
    ): T?

    fun <T : Widget> ancestorWidgetOfExactType(
        type: KClass<T>
    ): T?

    fun <T : State> ancestorStateOfType(type: KClass<T>): T?
}

inline fun <reified T : InheritedWidget> BuildContext.inheritFromWidgetOfExactType(): T? =
    inheritFromWidgetOfExactType(T::class)

inline fun <reified T : InheritedWidget> BuildContext.ancestorInheritedElementForWidgetOfExactType(): T? =
    ancestorInheritedElementForWidgetOfExactType(T::class)

inline fun <reified T : Widget> BuildContext.ancestorWidgetOfExactType(): T? =
    ancestorWidgetOfExactType(T::class)

inline fun <reified T : State> BuildContext.ancestorStateOfExactType(): T? =
    ancestorStateOfType(T::class)