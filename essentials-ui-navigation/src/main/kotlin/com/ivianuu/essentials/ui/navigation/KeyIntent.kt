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

package com.ivianuu.essentials.ui.navigation

import android.content.Intent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.android.AppContext
import kotlin.reflect.KClass

@Qualifier annotation class KeyIntentFactoryBinding<K : Any>

@Suppress("UNCHECKED_CAST")
@Macro
@GivenSetElement
inline fun <T : @KeyIntentFactoryBinding<K> (K) -> Intent,
        reified K : Any> keyIntentFactoryBindingImpl(
    @Given instance: T
): KeyIntentFactoryElement = (K::class to instance) as KeyIntentFactoryElement

typealias KeyIntentFactoryElement = Pair<KClass<*>, (Key) -> Intent>

@GivenFun
fun intentKeyHandler(
    key: Key,
    @Given appContext: AppContext,
    @Given intentFactories: Set<KeyIntentFactoryElement>
): Boolean {
    val intentFactory = intentFactories.toMap()[key::class]
    if (intentFactory != null) {
        val intent = intentFactory(key)
        appContext.startActivity(
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
    return intentFactory != null
}
