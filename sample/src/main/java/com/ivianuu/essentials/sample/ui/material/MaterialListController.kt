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

package com.ivianuu.essentials.sample.ui.material

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.epoxy.CheckboxListItem
import com.ivianuu.essentials.ui.epoxy.EditTextDialogListItem
import com.ivianuu.essentials.ui.epoxy.ListDivider
import com.ivianuu.essentials.ui.epoxy.ListHeader
import com.ivianuu.essentials.ui.epoxy.ListItem
import com.ivianuu.essentials.ui.epoxy.MultiSelectListDialogListItem
import com.ivianuu.essentials.ui.epoxy.PopupMenuListItem
import com.ivianuu.essentials.ui.epoxy.RadioButtonListItem
import com.ivianuu.essentials.ui.epoxy.SeekBarListItem
import com.ivianuu.essentials.ui.epoxy.SingleItemListDialogListItem
import com.ivianuu.essentials.ui.epoxy.SwitchListItem
import com.ivianuu.essentials.ui.epoxy.epoxyController
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.injekt.Inject

val materialListRoute = controllerRoute<MaterialListController>()

@Inject
class MaterialListController : ListController() {

    private var checkBoxState = true
    private var switchState = true
    private var radioButtonState = true
    private var seekBarValue = 50
    private var singleItemValue = "B"
    private var multiSelectValue = setOf("B", "C")

    override fun epoxyController() = epoxyController {
        ListItem(id = "first", title = "Hello")

        ListDivider(id = "top_divider")

        ListItem(
            id = "item_one",
            title = "This is a title",
            text = "This is a text"
        )

        ListItem(id = "item_two", title = "This is a title")

        ListHeader(text = "Toggles")

        CheckboxListItem(
            id = "check box",
            title = "Check box item",
            value = checkBoxState,
            onChange = {
                checkBoxState = it
                requestModelBuild()
            }
        )

        SwitchListItem(
            id = "switch",
            title = "Switch item",
            value = switchState,
            onChange = {
                switchState = it
                requestModelBuild()
            }
        )

        RadioButtonListItem(
            id = "radio_button",
            title = "Radio button item",
            value = radioButtonState,
            onChange = {
                radioButtonState = it
                requestModelBuild()
            }
        )

        ListHeader(text = "More")

        SeekBarListItem(
            id = "seek bar",
            title = "Seek bar",
            text = "default = 50",
            value = seekBarValue,
            onChange = { seekBarValue },
            min = 5,
            max = 150,
            inc = 5,
            valueTextProvider = { "$it px" }
        )

        SingleItemListDialogListItem(
            id = "single item list",
            title = "Single item list",
            entries = arrayOf("A", "B", "C"),
            entryValues = arrayOf("A", "B", "C"),
            value = singleItemValue,
            onSelected = {
                singleItemValue = it
                requestModelBuild()
            }
        )

        MultiSelectListDialogListItem(
            id = "multi select list",
            title = "Multi item list",
            entries = arrayOf("A", "B", "C"),
            entryValues = arrayOf("A", "B", "C"),
            values = multiSelectValue,
            onSelected = {
                multiSelectValue = it
                requestModelBuild()
            }
        )

        EditTextDialogListItem(
            id = "edit text",
            title = "Edit text",
            onInputCompleted = { d { "input $it" } }
        )

        PopupMenuListItem(
            id = "menu",
            title = "Menu item",
            items = listOf(
                PopupMenuItem(value = "A", title = "A"),
                PopupMenuItem(value = "B", title = "B"),
                PopupMenuItem(value = "C", title = "C")
            ),
            onCanceled = { d { "popup canceled" } },
            onItemSelected = { d { "$it selected" } }
        )
    }

}