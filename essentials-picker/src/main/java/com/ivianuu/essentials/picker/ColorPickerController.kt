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
import android.view.ViewGroup
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.essentials.util.ext.MaterialDialog
import com.ivianuu.materialdialogs.color.colorChooser
import com.ivianuu.traveler.result.sendResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorPickerKey(
    val titleRes: Int = R.string.dialog_title_color_picker,
    val preselect: Int = 0,
    val allowCustomArgb: Boolean = true,
    val showAlphaSelector: Boolean = false,
    override val resultCode: Int = RequestCodeGenerator.generate()
) : ControllerKey(ColorPickerController::class, NavOptions()
    .push(FadeChangeHandler(removesFromViewOnPush = false))), ResultKey<Int>

/**
 * Color picker controller
 */
class ColorPickerController : BaseController() {

    private val key by bindKey<ColorPickerKey>()

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ) = MaterialDialog()
        .title(key.titleRes)
        .colorChooser(
            initialSelection = if (key.preselect != 0) key.preselect else null,
            allowCustomArgb = key.allowCustomArgb,
            showAlphaSelector = key.showAlphaSelector
        ) { _, color -> travelerRouter.sendResult(key.resultCode, color) }
        .negativeButton(R.string.action_cancel)
        .view

}