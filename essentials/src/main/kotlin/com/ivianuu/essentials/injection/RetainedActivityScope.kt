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

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.ivianuu.essentials.util.getViewModel
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Scope
import com.ivianuu.injekt.android.getApplicationComponent
import com.ivianuu.injekt.android.getClosestComponentOrNull

@Scope
annotation class RetainedActivityScope {
    companion object
}

@Factory
class RetainedActivityComponentHolder : ViewModel() {
    var component: Component? = null
}

fun ComponentActivity.initRetainedActivityComponentIfNeeded(componentProvider: () -> Component) {
    val viewModel = getViewModel {
        getApplicationComponent()
            .get<RetainedActivityComponentHolder>()
    }

    if (viewModel.component == null) {
        viewModel.component = componentProvider()
    }
}

val ComponentActivity.retainedActivityComponent: Component
    get() = getViewModel<RetainedActivityComponentHolder> { error("call 'initRetainedActivityComponent { ... }' first") }
        .component!!

fun <T : Activity> T.RetainedActivityComponent(
    block: (ComponentBuilder.() -> Unit)? = null
): Component = Component {
    scopes(RetainedActivityScope)
    getClosestComponentOrNull()?.let { dependencies(it) }
    block?.invoke(this)
}
