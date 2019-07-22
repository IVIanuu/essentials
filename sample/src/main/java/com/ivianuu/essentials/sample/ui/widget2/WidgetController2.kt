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

package com.ivianuu.essentials.sample.ui.widget2

import com.ivianuu.essentials.sample.ui.widget2.exp.WidgetController
import com.ivianuu.essentials.sample.ui.widget2.layout.ListView
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.sample.ui.widget2.material.Checkbox
import com.ivianuu.essentials.sample.ui.widget2.material.RadioButton
import com.ivianuu.essentials.sample.ui.widget2.material.Switch

class WidgetController2 : WidgetController() {

    enum class ButtonType {
        CHECKBOX, RADIO, SWITCH;

        fun cycle(): ButtonType {
            return when (this) {
                CHECKBOX -> RADIO
                RADIO -> SWITCH
                SWITCH -> CHECKBOX
            }
        }
    }

    private val selectedIndices = mutableSetOf<Int>()
    private var lastType: ButtonType? = null

    override fun build(context: BuildContext): Widget {
        return ListView(
            children = (1..100).map { i ->
                ListItem(
                    title = "Title $i",
                    text = "Text $i",
                    secondaryAction = when (lastType?.cycle() ?: ButtonType.CHECKBOX) {
                        ButtonType.CHECKBOX -> {
                            lastType = ButtonType.CHECKBOX
                            Checkbox(
                                value = selectedIndices.contains(i),
                                onChange = {}
                            )
                        }
                        ButtonType.RADIO -> {
                            lastType = ButtonType.RADIO
                            RadioButton(
                                value = selectedIndices.contains(i),
                                onChange = {}
                            )
                        }
                        ButtonType.SWITCH -> {
                            lastType = ButtonType.SWITCH
                            Switch(
                                value = selectedIndices.contains(i),
                                onChange = {}
                            )
                        }
                    },
                    onClick = {
                        if (selectedIndices.contains(i)) {
                            selectedIndices.remove(i)
                        } else {
                            selectedIndices.add(i)
                        }

                        buildOwner?.rebuild()
                    }
                )
            }
        )
    }
}
