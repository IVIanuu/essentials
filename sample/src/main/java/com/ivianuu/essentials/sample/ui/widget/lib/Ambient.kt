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

package com.ivianuu.essentials.sample.ui.widget.lib

import android.content.Context
import android.view.ViewGroup

inline fun <reified T> ambientOf(): Ambient<T> = ambientOf(T::class)

fun <T> ambientOf(key: Any): Ambient<T> = Ambient(key)

class Ambient<T> @PublishedApi internal constructor(val key: Any) {

    operator fun invoke(context: BuildContext): T = context.getAmbient(this)!!

    inner class Provider<T>(
        val value: T,
        child: Widget
    ) : ProxyWidget(child = child, key = key)
}

val AndroidContextAmbient = ambientOf<Context>()

val ContainerAmbient = ambientOf<ViewGroup>()