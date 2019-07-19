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

package com.ivianuu.essentials.sample.ui.widget

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.layout.Stack
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetTestController : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_widget

    private var loading = true

    private val checkedIndices = mutableSetOf<Int>()

    private val buildContext = BuildContext(
        rootViewProvider = { view.cast() }
    ) {
        emit(
            AppBarScreen(
                appBar = Toolbar(title = "Widget screen"),
                content = if (loading) {
                    Loading()
                } else {
                    Stack { addListItems() }
                }
            )
        )
    }

    private fun BuildContext.addListItems() {
        var wasCheckBox = false
        (0..2).forEach { index ->
            emit(
                ListItem(
                    key = index,
                    title = "Title $index",
                    text = "Text $index",
                    primaryAction = Icon(
                        iconRes = R.drawable.es_ic_torch_on
                    ),
                    secondaryAction = if (wasCheckBox) {
                        RadioButton(
                            value = checkedIndices.contains(index),
                            onChange = { toggle(index) }
                        )
                    } else {
                        Checkbox(
                            value = checkedIndices.contains(index),
                            onChange = { toggle(index) }
                        )
                    },
                    onClick = { toggle(index) }
                )
            )

            wasCheckBox = !wasCheckBox
        }
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            delay(2000)
            loading = false
            buildContext.invalidate()
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        buildContext.invalidate()
    }

    private fun toggle(index: Int) {
        if (checkedIndices.contains(index)) {
            checkedIndices.remove(index)
        } else {
            checkedIndices.add(index)
        }
        buildContext.invalidate()
    }
}