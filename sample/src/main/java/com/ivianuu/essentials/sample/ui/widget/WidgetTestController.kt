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

import android.view.Gravity
import androidx.lifecycle.lifecycleScope
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.behavior.Alpha
import com.ivianuu.essentials.sample.ui.widget.behavior.BlockTouches
import com.ivianuu.essentials.sample.ui.widget.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget.layout.Column
import com.ivianuu.essentials.sample.ui.widget.layout.Container
import com.ivianuu.essentials.sample.ui.widget.layout.Padding
import com.ivianuu.essentials.sample.ui.widget.layout.Row
import com.ivianuu.essentials.sample.ui.widget.layout.Space
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.util.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetTestController : WidgetController() {
    private var loading = true

    private val checkedIndices = mutableSetOf<Int>()

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            delay(2000)
            loading = false
            buildContext?.invalidate()
        }
    }

    override fun BuildContext.buildChildren() {
        d { "build children" }
        emit(
            AppBarScreen(
                appBar = Toolbar(
                    title = Text(
                        text = "Widget screen",
                        textAppearance = R.style.TextAppearance_MaterialComponents_Headline6
                    ),
                    leading = IconButton(
                        iconRes = R.drawable.icon_back_white,
                        onClick = { navigator.pop() }
                    ),
                    trailing = listOf(
                        IconButton(
                            key = "link",
                            iconRes = R.drawable.es_ic_link,
                            onClick = { d { "link clicked" } }
                        ),
                        Space(width = dp(16).toInt()),
                        IconButton(
                            key = "torch",
                            iconRes = R.drawable.es_ic_torch_on,
                            onClick = { d { "torch clicked" } }
                        )
                    )
                ),
                content = if (loading) {
                    Loading()
                } else {
                    Column {
                        addListItems()
                        emit(
                            Row(gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP) {
                                emit(
                                    Padding(
                                        padding = dp(16).toInt(),
                                        child = MaterialButton(
                                            key = "1",
                                            text = "Button 1",
                                            onClick = {
                                                d { "button 1 clicked" }
                                                invalidate()
                                            }
                                        )
                                    )
                                )
                                emit(
                                    Space(
                                        key = "1",
                                        width = dp(8).toInt()
                                    )
                                )
                                emit(
                                    Alpha(
                                        alpha = 0.5f,
                                        child = MaterialButton(
                                            key = "2",
                                            text = "Button 2",
                                            onClick = {
                                                d { "button 2 clicked" }
                                            }
                                        )
                                    )
                                )
                                emit(
                                    Space(
                                        key = "2",
                                        width = dp(8).toInt()
                                    )
                                )
                                emit(
                                    BlockTouches(
                                        child = MaterialButton(
                                            key = "3",
                                            text = "Button 3",
                                            onClick = {
                                                d { "button 3 clicked" }
                                            }
                                        )
                                    )
                                )
                            }
                        )

                        emit(
                            Container(
                                height = dp(300).toInt(),
                                width = dp(300).toInt(),
                                gravity = Gravity.CENTER_VERTICAL or Gravity.END,
                                paddingRight = dp(32).toInt(),
                                child = MaterialButton(
                                    text = "Click me",
                                    onClick = {}
                                )
                            )
                        )
                    }
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

    private fun toggle(index: Int) {
        if (checkedIndices.contains(index)) {
            checkedIndices.remove(index)
        } else {
            checkedIndices.add(index)
        }
        buildContext?.invalidate()
    }
}