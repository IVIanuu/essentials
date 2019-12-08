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

package com.ivianuu.essentials.injection

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.ivianuu.essentials.util.getViewModel
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Scope
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.injekt.android.getApplicationComponent
import com.ivianuu.injekt.component

@Scope
annotation class RetainedActivityScope {
    companion object
}

@Factory
class RetainedActivityComponentHolder(
    @ApplicationScope private val applicationComponent: Component
) : ViewModel() {
    val component = component {
        dependencies(applicationComponent)
        scopes(RetainedActivityScope)
    }
}

val ComponentActivity.retainedActivityComponent: Component
    get() = getViewModel {
        getApplicationComponent()
            .get<RetainedActivityComponentHolder>()
    }.component