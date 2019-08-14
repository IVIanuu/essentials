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
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.setContent
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.activityComponent

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity(), InjektTrait {

    override val component by unsafeLazy {
        activityComponent {
            modules(this@EsActivity.modules())
        }
    }

    protected open val layoutRes get() = 0

    open val containerId
        get() = android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        setContent(findViewById(containerId)) {
            ComponentAmbient.Provider(component) {
                compose()
            }
        }
    }

    protected open fun modules(): List<Module> = emptyList()

    protected abstract fun ComponentComposition.compose()

}