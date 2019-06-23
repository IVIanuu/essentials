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

package com.ivianuu.essentials.sample.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.service.LifecycleService
import com.ivianuu.essentials.service.UiVisibleService
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.intoSet
import com.ivianuu.injekt.module
import com.ivianuu.injekt.withBinding

@Inject
class MyService : UiVisibleService {

    override fun uiVisible() {
        d { "ui visible" }
    }

    override fun uiInvisible() {
        d { "ui invisible" }
    }

}

@Inject
class MyLifecycleService : LifecycleService {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        d { "on state changed $source $event" }
    }
}

val myServiceModule = module {
    withBinding<MyService> {
        intoSet<MyService, UiVisibleService>()
    }
    withBinding<MyLifecycleService> {
        intoSet<MyLifecycleService, LifecycleService>()
    }
}