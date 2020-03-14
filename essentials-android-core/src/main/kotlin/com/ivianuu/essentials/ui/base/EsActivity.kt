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

package com.ivianuu.essentials.ui.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.ui.core.setContent
import com.ivianuu.essentials.ui.core.Environment
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.ComponentBuilderInterceptor
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.ComponentOwner
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.ForActivity
import com.ivianuu.injekt.single

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), ComponentOwner,
    ComponentBuilderInterceptor {

    override val component by unsafeLazy {
        ActivityComponent(this) {
            esActivityBindings()
            buildComponent()
        }
    }

    protected open val layoutRes: Int get() = 0

    protected open val containerId: Int
        get() = android.R.id.content

    private val retainedObjects =
        RetainedObjects()

    private lateinit var composition: Composition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        composition = findViewById<ViewGroup>(containerId).setContent {
            WrapContentWithEnvironment {
                content()
            }
        }
    }

    override fun onDestroy() {
        retainedObjects.dispose()
        composition.dispose()
        super.onDestroy()
    }

    @Composable
    protected open fun WrapContentWithEnvironment(children: @Composable () -> Unit) {
        Environment(
            activity = this,
            component = component,
            retainedObjects = retainedObjects,
            children = children
        )
    }

    @Composable
    protected abstract fun content()
}

private fun ComponentBuilder.esActivityBindings() {
    single {
        NavigatorState(
            coroutineScope = get(qualifier = ForActivity),
            logger = get()
        )
    }
}
