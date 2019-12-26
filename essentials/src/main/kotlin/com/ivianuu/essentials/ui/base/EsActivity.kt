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
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.setContent
import com.ivianuu.essentials.ui.core.ActivityAmbient
import com.ivianuu.essentials.ui.core.Environment
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait {

    override val component by unsafeLazy {
        ActivityComponent {
            modules(EsActivityModule(this@EsActivity))
            modules(this@EsActivity.modules())
        }
    }

    protected open val layoutRes: Int get() = 0

    protected open val containerId: Int
        get() = android.R.id.content

    private val retainedObjects =
        RetainedObjects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        findViewById<ViewGroup>(containerId).setContent {
            WrapContentWithEnvironment {
                content()
            }
        }
    }

    override fun onDestroy() {
        // todo use disposeComposition once fixed
        retainedObjects.dispose()
        findViewById<ViewGroup>(containerId).setContent { }
        super.onDestroy()
    }

    @Composable
    protected open fun WrapContentWithEnvironment(content: @Composable() () -> Unit) {
        Environment(
            component = component,
            retainedObjects = retainedObjects
        ) {
            ActivityAmbient.Provider(this) {
                content()
            }
        }
    }

    @Composable
    protected abstract fun content()

    protected open fun modules(): List<Module> = emptyList()
}

private fun EsActivityModule(activity: EsActivity) = Module {
    single { NavigatorState(coroutineScope = activity.lifecycleScope) }
}
