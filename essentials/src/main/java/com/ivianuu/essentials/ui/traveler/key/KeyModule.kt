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

package com.ivianuu.essentials.ui.traveler.key

import android.os.Bundle
import android.os.Parcelable
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.injekt.typeOf
import kotlin.reflect.KClass

/**
 * Module to bind a traveler key
 */
fun keyModule(
    bundle: Bundle?,
    throwIfNotAvailable: Boolean = true
) = module {
    if (bundle != null && bundle.containsKey(TRAVELER_KEY_CLASS) && bundle.containsKey(TRAVELER_KEY)) {
        val className = bundle.getString(TRAVELER_KEY_CLASS)!!
        val type = Class.forName(className).kotlin as KClass<Any>
        single(typeOf(type)) { bundle.getParcelable<Parcelable>(TRAVELER_KEY)!! }
    } else if (throwIfNotAvailable) {
        error("No traveler key in bundle $bundle")
    }
}