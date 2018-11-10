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

package com.ivianuu.essentials.ui.common

import android.os.Bundle
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination
import com.ivianuu.essentials.ui.traveler.key.BaseFragmentDestination
import com.ivianuu.essentials.ui.traveler.key.bindDestination
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorPickerDestination(
    val titleRes: Int = R.string.dialog_title_color_picker,
    val preselect: Int = 0,
    override val resultCode: Int = RequestCodeGenerator.generate()
) : BaseFragmentDestination(ColorPickerFragment::class), ResultDestination<Int>

/**
 * Color picker fragment
 */
// todo remove/ move to somewhere else
class ColorPickerFragment : BaseFragment(), ColorChooserDialog.ColorCallback {

    private val destination by bindDestination<ColorPickerDestination>()

    private var colorSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ColorChooserDialog.Builder(requireContext(), destination.titleRes)
            .apply {
                if (destination.preselect != 0) {
                    preselect(destination.preselect)
                }
            }
            .show(childFragmentManager)
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        router.goBackWithResult(destination.resultCode, selectedColor)
        colorSelected = true
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
        if (!colorSelected) {
            router.goBack()
        }
    }
}