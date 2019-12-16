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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import com.ivianuu.essentials.injection.RetainedActivityComponent
import com.ivianuu.essentials.injection.initRetainedActivityComponentIfNeeded
import com.ivianuu.essentials.injection.retainedActivityComponent
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityModule
import com.ivianuu.injekt.android.ActivityScope

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait {

    override val component by unsafeLazy {
        Component {
            scopes(ActivityScope)
            dependencies(retainedActivityComponent)
            modules(ActivityModule())
            modules(this@EsActivity.modules())
        }
    }

    protected open val layoutRes: Int get() = 0
    protected open val drawEdgeToEdge: Boolean get() = false

    open val containerId: Int
        get() = android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRetainedActivityComponentIfNeeded {
            RetainedActivityComponent {
                modules(retainedModules())
            }
        }

        if (drawEdgeToEdge) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = Color.Black.copy(alpha = 0.25f).toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }
    }

    protected open fun modules(): List<Module> = emptyList()

    protected open fun retainedModules(): List<Module> = emptyList()
}
