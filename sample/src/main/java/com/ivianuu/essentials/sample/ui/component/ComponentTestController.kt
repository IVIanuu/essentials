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

package com.ivianuu.essentials.sample.ui.component

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.component.lib.ComponentContext
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.scopes.android.onPause
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ComponentTestController : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_component

    private var checked = true
    private var loading = true
    private var textCount = 0

    private val context = ComponentContext(
        rootViewId = R.id.content,
        rootViewProvider = { view.cast() }
    ) {
        if (loading) {
            emit(Loading("loading"), R.id.content)
        } else {
            emit(
                TextWithContent(
                    id = "text with content",
                    text = "Current state is $checked count is $textCount",
                    content = Checkbox(
                        id = "checkbox",
                        value = checked,
                        onChange = {
                            checked = it
                            componentContext.invalidate()
                        }
                    )
                ),
                R.id.content
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            delay(2000)
            loading = false
            context.invalidate()
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        context.invalidate()

        onPause.coroutineScope.launch {
            while (coroutineContext.isActive) {
                delay(1000)
                textCount += 1
                context.invalidate()
            }
        }
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        context.cancelAll()
    }
}