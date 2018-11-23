/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.traveler.anim.DialogControllerKeySetup
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorPickerKey(
    val titleRes: Int = R.string.dialog_title_color_picker,
    val preselect: Int = 0,
    override val resultCode: Int = RequestCodeGenerator.generate()
) : ControllerKey(ColorPickerController::class, DialogControllerKeySetup()), ResultKey<Int>

/**
 * Color picker controller
 */
class ColorPickerController : BaseController(), ColorChooserDialog.ColorCallback {

    private val key by bindKey<ColorPickerKey>()

    private var colorSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ColorChooserDialog.Builder(activity, key.titleRes)
            .apply {
                if (key.preselect != 0) {
                    preselect(key.preselect)
                }
            }
            .show(activity.supportFragmentManager)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = View(activity)

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        travelerRouter.goBackWithResult(key.resultCode, selectedColor)
        colorSelected = true
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
        if (!colorSelected) {
            travelerRouter.goBack()
        }
    }
}