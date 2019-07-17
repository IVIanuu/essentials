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

package com.ivianuu.essentials.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.dialog
import com.ivianuu.essentials.util.Properties
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun dialogRoute(
    name: String? = null,
    extras: Properties = Properties(),
    options: ControllerRoute.Options? = ControllerRoute.Options().dialog(),
    block: MaterialDialog.(Navigator) -> Unit
) = controllerRoute<MdDialogController>(name, extras, options) {
    parametersOf(block)
}

@Inject
internal class MdDialogController(
    @Param private val block: MaterialDialog.(Navigator) -> Unit
) : EsDialogController() {
    override fun onCreateDialog(inflater: LayoutInflater, container: ViewGroup) = dialog {
        block(this, navigator)
    }
}