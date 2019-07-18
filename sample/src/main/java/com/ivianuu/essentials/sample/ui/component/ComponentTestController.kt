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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.component.lib.ComponentContext
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComponentTestController : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_component

    private var loading = true

    private val checkedIndices = mutableSetOf<Int>()

    private val context = ComponentContext(
        rootViewId = R.id.content,
        rootViewProvider = { view.cast() }
    ) {
        d { "build models" }
        if (loading) {
            emit(Loading(id = "loading"))
        } else {
            /*emit(Checkbox(id = "lol", value = checkedIndices.isEmpty(), onChange = {
                if (checkedIndices.isEmpty()) {
                    checkedIndices.add(0)
                } else {
                    checkedIndices.clear()
                }

                componentContext.invalidate()
            }))*/
            emit(ListView(id = "list") {
                var wasCheckBox = false
                (0..5).forEach { index ->
                    emit(
                        ListItem(
                            id = "list $index",
                            title = "Title $index",
                            text = "Text $index",
                            secondaryAction = if (wasCheckBox) {
                                RadioButton(
                                    id = "radio",
                                    value = checkedIndices.contains(index),
                                    onChange = { toggle(index) }
                                )
                            } else {
                                Checkbox(
                                    id = "checkbox",
                                    value = checkedIndices.contains(index),
                                    onChange = { toggle(index) }
                                )
                            },
                            onClick = { toggle(index) }
                        )
                    )

                    wasCheckBox = !wasCheckBox
                }
            })
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
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        context.cancelAll()
    }

    private fun toggle(index: Int) {
        if (checkedIndices.contains(index)) {
            checkedIndices.remove(index)
        } else {
            checkedIndices.add(index)
        }
        context.invalidate()
    }
}