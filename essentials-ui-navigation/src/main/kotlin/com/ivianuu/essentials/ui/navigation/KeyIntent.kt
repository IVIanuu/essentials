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
import com.ivianuu.injekt.android.ApplicationContext
import kotlin.reflect.KClass

inline fun <reified K : Any, T : (K) -> Intent> keyIntentFactoryBinding():
        @GivenSetElement (@Given () -> T) -> KeyIntentFactoryBinding = {
    @Suppress("UNCHECKED_CAST")
    K::class to it as () -> (Key) -> Intent
}

typealias KeyIntentFactoryBinding = Pair<KClass<*>, () -> (Key) -> Intent>

@GivenFun
fun intentKeyHandler(
    key: Key,
    @Given applicationContext: ApplicationContext,
    @Given intentFactories: Set<KeyIntentFactoryBinding>
): Boolean {
    val intentFactory = intentFactories.toMap()[key::class]?.invoke()
    if (intentFactory != null) {
        val intent = intentFactory(key)
        applicationContext.startActivity(
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
    return intentFactory != null
}
